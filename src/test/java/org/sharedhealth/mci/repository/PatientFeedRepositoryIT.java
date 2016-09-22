package org.sharedhealth.mci.repository;

import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sharedhealth.mci.BaseIntegrationTest;
import org.sharedhealth.mci.config.MCICassandraConfig;
import org.sharedhealth.mci.model.PatientUpdateLog;
import org.sharedhealth.mci.util.TestUtils;
import org.sharedhealth.mci.util.TimeUuidUtil;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.sharedhealth.mci.util.Constants.EVENT_TYPE_CREATED;
import static org.sharedhealth.mci.util.Constants.EVENT_TYPE_UPDATED;

public class PatientFeedRepositoryIT extends BaseIntegrationTest {
    private static final String HEALTH_ID = "HEALTH_ID";
    private static final String CREATED_BY = "{\"facility\":\"Bahmni\",\"provider\":\"Dr. Monika\"";

    private PatientFeedRepository feedRepository;
    private Mapper<PatientUpdateLog> updateLogMapper;

    @Before
    public void setup() throws ExecutionException, InterruptedException {
        MappingManager mappingManager = MCICassandraConfig.getInstance().getMappingManager();
        feedRepository = new PatientFeedRepository(mappingManager);
        updateLogMapper = mappingManager.mapper(PatientUpdateLog.class);
    }

    @After
    public void tearDown() throws Exception {
        TestUtils.truncateAllColumnFamilies();
    }

    @Test
    public void shouldFindPatentLogAfterLastMarker() throws Exception {
        UUID eventId1 = TimeUuidUtil.uuidForDate(new Date());
        UUID eventId2 = TimeUuidUtil.uuidForDate(new Date());
        UUID eventId3 = TimeUuidUtil.uuidForDate(new Date());
        PatientUpdateLog updateLog1 = buildPatientUpdateLog(eventId1, EVENT_TYPE_CREATED);
        PatientUpdateLog updateLog2 = buildPatientUpdateLog(eventId2, EVENT_TYPE_UPDATED);
        PatientUpdateLog updateLog3 = buildPatientUpdateLog(eventId3, EVENT_TYPE_UPDATED);

        updateLogMapper.save(updateLog1);
        updateLogMapper.save(updateLog2);
        updateLogMapper.save(updateLog3);

        PatientUpdateLog log = feedRepository.findPatientUpdateLog(null);
        assertEquals(log.getEventId(), updateLog1.getEventId());
        assertEquals(log.getHealthId(), updateLog1.getHealthId());

        log = feedRepository.findPatientUpdateLog(eventId1);
        assertEquals(log.getEventId(), updateLog2.getEventId());
        assertEquals(log.getHealthId(), updateLog2.getHealthId());

        log = feedRepository.findPatientUpdateLog(eventId2);
        assertEquals(log.getEventId(), updateLog3.getEventId());
        assertEquals(log.getHealthId(), updateLog3.getHealthId());
    }

    private PatientUpdateLog buildPatientUpdateLog(UUID eventId1, String eventType) {
        PatientUpdateLog updateLog = new PatientUpdateLog();
        updateLog.setEventId(eventId1);
        updateLog.setHealthId(HEALTH_ID);
        updateLog.setEventType(eventType);
        updateLog.setChangeSet("   ");
        updateLog.setApprovedBy(CREATED_BY);
        updateLog.setRequestedBy(CREATED_BY);
        return updateLog;
    }
}