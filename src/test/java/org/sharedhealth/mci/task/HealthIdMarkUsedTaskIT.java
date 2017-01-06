package org.sharedhealth.mci.task;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.sharedhealth.mci.BaseIntegrationTest;
import org.sharedhealth.mci.WebClient;
import org.sharedhealth.mci.config.MCICassandraConfig;
import org.sharedhealth.mci.config.MCIProperties;
import org.sharedhealth.mci.model.FailedEvent;
import org.sharedhealth.mci.model.IdentityStore;
import org.sharedhealth.mci.model.Marker;
import org.sharedhealth.mci.model.PatientUpdateLog;
import org.sharedhealth.mci.repository.FailedEventRepository;
import org.sharedhealth.mci.repository.MarkerRepository;
import org.sharedhealth.mci.repository.PatientFeedRepository;
import org.sharedhealth.mci.service.HealthIdMarkUsedService;
import org.sharedhealth.mci.service.IdentityProviderService;
import org.sharedhealth.mci.util.TestUtils;
import org.sharedhealth.mci.util.TimeUuidUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.sharedhealth.mci.util.Constants.*;
import static org.sharedhealth.mci.util.HttpUtil.*;

public class HealthIdMarkUsedTaskIT extends BaseIntegrationTest {
    private final String SIGN_IN_URL = "/signin";
    private String HID_MARK_USED_URL = "/healthIds/markUsed/";

    private Mapper<PatientUpdateLog> patientUpdateLogMapper;
    private Mapper<Marker> markerMapper;
    private Mapper<FailedEvent> failedEventMapper;
    private MCIProperties mciProperties;
    private MappingManager mappingManager;
    private HealthIdMarkUsedTask healthIdMarkUsedTask;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9997);

    @After
    public void tearDown() throws Exception {
        TestUtils.truncateAllColumnFamilies();
    }

    @Before
    public void setUp() throws Exception {
        mappingManager = MCICassandraConfig.getInstance().getMappingManager();
        patientUpdateLogMapper = mappingManager.mapper(PatientUpdateLog.class);
        markerMapper = mappingManager.mapper(Marker.class);
        failedEventMapper = mappingManager.mapper(FailedEvent.class);
        mciProperties = MCIProperties.getInstance();

        IdentityStore identityStore = new IdentityStore();
        WebClient webClient = new WebClient(identityStore);
        IdentityProviderService identityProviderService = new IdentityProviderService(webClient, identityStore);
        HealthIdMarkUsedService markUsedService = new HealthIdMarkUsedService(identityProviderService, webClient, mciProperties);
        healthIdMarkUsedTask = new HealthIdMarkUsedTask(markUsedService, new PatientFeedRepository(mappingManager),
                new MarkerRepository(mappingManager), new FailedEventRepository(mappingManager), mciProperties);
    }

    @Test
    public void shouldMarkCreatedPatientHealthIdAsUsed() throws Exception {
        UUID token = UUID.randomUUID();
        UUID eventId = TimeUuidUtil.uuidForDate(new Date());
        PatientUpdateLog patientUpdateLog = buildPatientUpdateLog(eventId, EVENT_TYPE_CREATED, "HID");
        patientUpdateLogMapper.save(patientUpdateLog);

        setUpIdentityStub(token);
        setupMarkUsedStub(token);

        healthIdMarkUsedTask.process();

        verify(1, postRequestedFor(urlMatching(SIGN_IN_URL)));
        verify(1, putRequestedFor(urlMatching(HID_MARK_USED_URL + "HID"))
                .withRequestBody(equalToJson("{used_at:" + eventId.toString() + "}")));
    }

    @Test
    public void shouldMarkHealthIdUsedAfterLastMarker() throws Exception {
        UUID token = UUID.randomUUID();
        UUID eventId1 = TimeUuidUtil.uuidForDate(new Date());
        patientUpdateLogMapper.save(buildPatientUpdateLog(eventId1, EVENT_TYPE_CREATED, "HID1"));

        Marker marker = new Marker();
        marker.setType(HEALTH_ID_MARK_USED_MARKER);
        marker.setCreatedAt(TimeUuidUtil.uuidForDate(new Date()));
        marker.setValue(eventId1.toString());
        markerMapper.save(marker);

        UUID eventId2 = TimeUuidUtil.uuidForDate(new Date());
        patientUpdateLogMapper.save(buildPatientUpdateLog(eventId2, EVENT_TYPE_CREATED, "HID2"));

        setUpIdentityStub(token);
        setupMarkUsedStub(token);

        healthIdMarkUsedTask.process();

        verify(1, postRequestedFor(urlMatching(SIGN_IN_URL)));
        verify(1, putRequestedFor(urlMatching(HID_MARK_USED_URL + "HID2"))
                .withRequestBody(equalToJson("{used_at:" + eventId2.toString() + "}")));
        assertMarker(eventId2);
    }

    @Test
    public void shouldMarkMultipleHealthIdsAsUsedAccordingToBlockSize() throws Exception {
        UUID token = UUID.randomUUID();
        UUID eventId1 = TimeUuidUtil.uuidForDate(new Date());
        patientUpdateLogMapper.save(buildPatientUpdateLog(eventId1, EVENT_TYPE_CREATED, "HID1"));

        UUID eventId2 = TimeUuidUtil.uuidForDate(new Date());
        patientUpdateLogMapper.save(buildPatientUpdateLog(eventId2, EVENT_TYPE_CREATED, "HID2"));

        UUID eventId3 = TimeUuidUtil.uuidForDate(new Date());
        patientUpdateLogMapper.save(buildPatientUpdateLog(eventId3, EVENT_TYPE_CREATED, "HID3"));

        UUID eventId4 = TimeUuidUtil.uuidForDate(new Date());
        patientUpdateLogMapper.save(buildPatientUpdateLog(eventId4, EVENT_TYPE_CREATED, "HID4"));

        UUID eventId5 = TimeUuidUtil.uuidForDate(new Date());
        patientUpdateLogMapper.save(buildPatientUpdateLog(eventId5, EVENT_TYPE_CREATED, "HID5"));

        setUpIdentityStub(token);
        setupMarkUsedStub(token);

        healthIdMarkUsedTask.process();

        verify(1, postRequestedFor(urlMatching(SIGN_IN_URL)));
        verify(1, putRequestedFor(urlMatching(HID_MARK_USED_URL + "HID1"))
                .withRequestBody(equalToJson("{used_at:" + eventId1.toString() + "}")));
        verify(1, putRequestedFor(urlMatching(HID_MARK_USED_URL + "HID2"))
                .withRequestBody(equalToJson("{used_at:" + eventId2.toString() + "}")));
        verify(1, putRequestedFor(urlMatching(HID_MARK_USED_URL + "HID3"))
                .withRequestBody(equalToJson("{used_at:" + eventId3.toString() + "}")));
        verify(1, putRequestedFor(urlMatching(HID_MARK_USED_URL + "HID4"))
                .withRequestBody(equalToJson("{used_at:" + eventId4.toString() + "}")));
        verify(1, putRequestedFor(urlMatching(HID_MARK_USED_URL + "HID5"))
                .withRequestBody(equalToJson("{used_at:" + eventId5.toString() + "}")));
        assertMarker(eventId5);
    }

    @Test
    public void shouldMarkHealthIdAsUsedOnlyForCreateEvents() throws Exception {
        UUID token = UUID.randomUUID();
        UUID eventId1 = TimeUuidUtil.uuidForDate(new Date());
        patientUpdateLogMapper.save(buildPatientUpdateLog(eventId1, EVENT_TYPE_CREATED, "HID1"));

        UUID eventId2 = TimeUuidUtil.uuidForDate(new Date());
        patientUpdateLogMapper.save(buildPatientUpdateLog(eventId2, EVENT_TYPE_CREATED, "HID2"));

        UUID eventId3 = TimeUuidUtil.uuidForDate(new Date());
        patientUpdateLogMapper.save(buildPatientUpdateLog(eventId3, EVENT_TYPE_UPDATED, "HID1"));

        UUID eventId4 = TimeUuidUtil.uuidForDate(new Date());
        patientUpdateLogMapper.save(buildPatientUpdateLog(eventId4, EVENT_TYPE_CREATED, "HID3"));

        UUID eventId5 = TimeUuidUtil.uuidForDate(new Date());
        patientUpdateLogMapper.save(buildPatientUpdateLog(eventId5, EVENT_TYPE_UPDATED, "HID3"));

        setUpIdentityStub(token);
        setupMarkUsedStub(token);

        healthIdMarkUsedTask.process();

        verify(1, postRequestedFor(urlMatching(SIGN_IN_URL)));
        verify(1, putRequestedFor(urlMatching(HID_MARK_USED_URL + "HID1"))
                .withRequestBody(equalToJson("{used_at:" + eventId1.toString() + "}")));
        verify(1, putRequestedFor(urlMatching(HID_MARK_USED_URL + "HID2"))
                .withRequestBody(equalToJson("{used_at:" + eventId2.toString() + "}")));
        verify(1, putRequestedFor(urlMatching(HID_MARK_USED_URL + "HID3"))
                .withRequestBody(equalToJson("{used_at:" + eventId4.toString() + "}")));
        assertMarker(eventId5);
    }

    @Test
    public void shouldPutIntoFailedEventsIfFails() throws Exception {
        UUID eventId = TimeUuidUtil.uuidForDate(new Date());
        PatientUpdateLog patientUpdateLog = buildPatientUpdateLog(eventId, EVENT_TYPE_CREATED, "HID");
        patientUpdateLogMapper.save(patientUpdateLog);

        healthIdMarkUsedTask.process();

        assertFailedEvent(eventId, 0);
        assertMarker(eventId);
        verify(1, postRequestedFor(urlMatching(SIGN_IN_URL)));
        verify(0, putRequestedFor(urlMatching(HID_MARK_USED_URL)));
    }

    @Test
    public void shouldProcessFailedEventsAndDelete() throws Exception {
        UUID eventId = TimeUuidUtil.uuidForDate(new Date());
        failedEventMapper.save(new FailedEvent(FAILURE_TYPE_HEALTH_MARK_USED, eventId, "ERROR"));
        patientUpdateLogMapper.save(buildPatientUpdateLog(eventId, EVENT_TYPE_CREATED, "HID"));
        UUID token = UUID.randomUUID();
        setUpIdentityStub(token);
        setupMarkUsedStub(token);

        healthIdMarkUsedTask.processFailedEvents();

        assertTrue(mappingManager.getSession().execute(select().from(CF_FAILED_EVENTS)).isExhausted());
        verify(1, postRequestedFor(urlMatching(SIGN_IN_URL)));
        verify(0, putRequestedFor(urlMatching(HID_MARK_USED_URL)));
    }

    @Test
    public void shouldIncreaseRetriesForFailedEventIfFailsWhileRetry() throws Exception {
        UUID eventId = TimeUuidUtil.uuidForDate(new Date());
        failedEventMapper.save(new FailedEvent(FAILURE_TYPE_HEALTH_MARK_USED, eventId, "ERROR"));
        patientUpdateLogMapper.save(buildPatientUpdateLog(eventId, EVENT_TYPE_CREATED, "HID"));

        healthIdMarkUsedTask.processFailedEvents();
        healthIdMarkUsedTask.processFailedEvents();
        healthIdMarkUsedTask.processFailedEvents();

        assertFailedEvent(eventId, 3);
        verify(3, postRequestedFor(urlMatching(SIGN_IN_URL)));
        verify(0, putRequestedFor(urlMatching(HID_MARK_USED_URL)));
    }

    @Test
    public void shouldNotRetryFailedEventIfItHasReachedMaxRetryLimit() throws Exception {
        UUID eventId = TimeUuidUtil.uuidForDate(new Date());
        failedEventMapper.save(new FailedEvent(FAILURE_TYPE_HEALTH_MARK_USED, eventId, "ERROR", mciProperties.getFailedEventRetryLimit()));
        patientUpdateLogMapper.save(buildPatientUpdateLog(eventId, EVENT_TYPE_CREATED, "HID"));

        healthIdMarkUsedTask.processFailedEvents();
        healthIdMarkUsedTask.processFailedEvents();
        healthIdMarkUsedTask.processFailedEvents();

        assertFailedEvent(eventId, mciProperties.getMaxFailedEventLimit());
        verify(0, postRequestedFor(urlMatching(SIGN_IN_URL)));
        verify(0, putRequestedFor(urlMatching(HID_MARK_USED_URL)));
    }

    @Test
    public void shouldNotProcessNewEventsIfFailedEventsReachToMaxLimit() throws Exception {
        for (int i = 0; i < mciProperties.getMaxFailedEventLimit(); i++) {
            failedEventMapper.save(new FailedEvent(FAILURE_TYPE_HEALTH_MARK_USED, TimeUuidUtil.uuidForDate(new Date()), "ERROR"));
        }

        healthIdMarkUsedTask.process();

        verify(0, postRequestedFor(urlMatching(SIGN_IN_URL)));
        verify(0, putRequestedFor(urlMatching(HID_MARK_USED_URL)));
    }

    private void assertFailedEvent(UUID eventId, int retriesExpected) {
        ResultSet resultSet = mappingManager.getSession().execute(select().from(CF_FAILED_EVENTS));
        List<FailedEvent> failedEvents = failedEventMapper.map(resultSet).all();
        assertEquals(1, failedEvents.size());
        FailedEvent failedEvent = failedEvents.get(0);
        assertEquals(FAILURE_TYPE_HEALTH_MARK_USED, failedEvent.getFailureType());
        assertEquals(eventId, failedEvent.getEventId());
        assertEquals(retriesExpected, failedEvent.getRetries());
    }

    private void assertMarker(UUID eventId) {
        ResultSet resultSet = mappingManager.getSession().execute(select(MARKER).from(CF_MARKER).where(eq(TYPE, HEALTH_ID_MARK_USED_MARKER)));
        String marker = resultSet.one().getString(0);
        assertEquals(eventId.toString(), marker);
    }

    private PatientUpdateLog buildPatientUpdateLog(UUID eventId, String eventType, String hid) {
        PatientUpdateLog patientUpdateLog = new PatientUpdateLog();
        patientUpdateLog.setEventId(eventId);
        patientUpdateLog.setEventType(eventType);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        patientUpdateLog.setYear(calendar.get(Calendar.YEAR));
        patientUpdateLog.setHealthId(hid);
        return patientUpdateLog;
    }

    private void setUpIdentityStub(UUID uuid) {
        String idpResponse = "{\"access_token\" : \"" + uuid.toString() + "\"}";

        stubFor(post(urlMatching(SIGN_IN_URL))
                .withHeader(X_AUTH_TOKEN_KEY, equalTo(mciProperties.getIdpXAuthToken()))
                .withHeader(CLIENT_ID_KEY, equalTo(mciProperties.getIdpClientId()))
                .withRequestBody(containing("password=password&email=shrSysAdmin%40gmail.com"))
                .willReturn(aResponse()
                                .withStatus(HttpStatus.SC_OK)
                                .withBody(idpResponse)
                ));
    }

    private void setupMarkUsedStub(UUID token) {
        stubFor(put(urlPathEqualTo(HID_MARK_USED_URL))
                .withHeader(X_AUTH_TOKEN_KEY, equalTo(token.toString()))
                .withHeader(CLIENT_ID_KEY, equalTo(mciProperties.getIdpClientId()))
                .withHeader(FROM_KEY, equalTo(mciProperties.getIdpEmail()))
                .willReturn(aResponse()
                                .withStatus(HttpStatus.SC_OK)
                                .withBody("Accepted")
                ));
    }
}