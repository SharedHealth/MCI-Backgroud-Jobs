package org.sharedhealth.mci.service;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sharedhealth.mci.BaseIntegrationTest;
import org.sharedhealth.mci.config.MCICassandraConfig;
import org.sharedhealth.mci.event.DuplicatePatientCreateEventProcessor;
import org.sharedhealth.mci.event.DuplicatePatientEventProcessorFactory;
import org.sharedhealth.mci.event.DuplicatePatientRetireEventProcessor;
import org.sharedhealth.mci.event.DuplicatePatientUpdateEventProcessor;
import org.sharedhealth.mci.model.*;
import org.sharedhealth.mci.repository.DuplicatePatientRepository;
import org.sharedhealth.mci.repository.MarkerRepository;
import org.sharedhealth.mci.repository.PatientFeedRepository;
import org.sharedhealth.mci.repository.PatientRepository;
import org.sharedhealth.mci.rule.*;
import org.sharedhealth.mci.util.Constants;
import org.sharedhealth.mci.util.TestUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static java.util.Arrays.asList;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.sharedhealth.mci.rule.DuplicatePatientRule.DUPLICATE_REASON_NID;
import static org.sharedhealth.mci.util.Constants.*;
import static org.sharedhealth.mci.util.TimeUuidUtil.uuidForDate;


public class DuplicatePatientFeedServiceIT extends BaseIntegrationTest {
    private MappingManager mappingManager;
    private Mapper<Patient> patientMapper;
    private Mapper<PatientUpdateLog> updateLogMapper;
    private Mapper<DuplicatePatient> duplicatePatientMapper;
    private Mapper<NidMapping> nidMapper;
    private Mapper<BrnMapping> brnMapper;
    private Mapper<UidMapping> uidMapper;
    private Mapper<NameMapping> nameMapper;
    private Mapper<Marker> markerMapper;
    private MarkerRepository markerRepository;

    private DuplicatePatientFeedService duplicatePatientFeedService;

    @Before
    public void setUp() throws Exception {
        mappingManager = MCICassandraConfig.getInstance().getMappingManager();
        PatientRepository patientRepository = new PatientRepository(mappingManager);

        markerRepository = new MarkerRepository(mappingManager);
        PatientFeedRepository patientFeedRepository = new PatientFeedRepository(mappingManager);

        DuplicatePatientBrnRule brnRule = new DuplicatePatientBrnRule(patientRepository);
        DuplicatePatientNidRule nidRule = new DuplicatePatientNidRule(patientRepository);
        DuplicatePatientUidRule uidRule = new DuplicatePatientUidRule(patientRepository);
        DuplicatePatientNameAndAddressRule nameAndAddressRule = new DuplicatePatientNameAndAddressRule(patientRepository);
        DuplicatePatientRuleEngine ruleEngine = new DuplicatePatientRuleEngine(asList(brnRule, nidRule, uidRule, nameAndAddressRule));
        DuplicatePatientRepository duplicateRepository = new DuplicatePatientRepository(mappingManager, patientRepository);

        DuplicatePatientCreateEventProcessor createEventProcessor = new DuplicatePatientCreateEventProcessor(ruleEngine, duplicateRepository);
        DuplicatePatientUpdateEventProcessor updateEventProcessor = new DuplicatePatientUpdateEventProcessor(ruleEngine, duplicateRepository);
        DuplicatePatientRetireEventProcessor retireEventProcessor = new DuplicatePatientRetireEventProcessor(ruleEngine, duplicateRepository);
        DuplicatePatientEventProcessorFactory duplicatePatientEventProcessorFactory = new DuplicatePatientEventProcessorFactory(createEventProcessor, updateEventProcessor, retireEventProcessor);
        duplicatePatientFeedService = new DuplicatePatientFeedService(patientFeedRepository, markerRepository, duplicatePatientEventProcessorFactory);

        patientMapper = mappingManager.mapper(Patient.class);
        duplicatePatientMapper = mappingManager.mapper(DuplicatePatient.class);
        nidMapper = mappingManager.mapper(NidMapping.class);
        brnMapper = mappingManager.mapper(BrnMapping.class);
        uidMapper = mappingManager.mapper(UidMapping.class);
        nameMapper = mappingManager.mapper(NameMapping.class);
        markerMapper = mappingManager.mapper(Marker.class);
        updateLogMapper = mappingManager.mapper(PatientUpdateLog.class);
    }

    @After
    public void tearDown() throws Exception {
        TestUtils.truncateAllColumnFamilies();
    }

    @Test
    public void shouldMarkPatientAsDuplicate() throws Exception {
        createPatient("hid1", "10", "11", "12", "nid1", "brn1", "uid1", "FName1", "SName1");
        createPatient("hid2", "10", "11", "12", "nid1", "brn2", "uid2", "FName2", "SName2");

        UUID createEventId = uuidForDate(new Date());
        createPatientUpdateLog(createEventId, Constants.EVENT_TYPE_CREATED, "", "hid1");

        duplicatePatientFeedService.processDuplicatePatients();

        List<DuplicatePatient> duplicates = findAllDuplicates();
        assertEquals(2, duplicates.size());

        assertTrue(duplicateExist(duplicates, "hid1", "hid2", "A10B11", DUPLICATE_REASON_NID));
        assertTrue(duplicateExist(duplicates, "hid1", "hid2", "A10B11C12", DUPLICATE_REASON_NID));
        assertMarker(createEventId.toString());
    }

    @Test
    public void shouldMarkPatientAsDuplicateAfterLastMarker() throws Exception {
        createPatient("hid1", "10", "11", "12", "nid1", "brn1", "uid1", "FName1", "SName1");
        UUID firstPatientCreateEventId = uuidForDate(new Date());
        createPatientUpdateLog(firstPatientCreateEventId, Constants.EVENT_TYPE_CREATED, "", "hid1");
        saveMarker(firstPatientCreateEventId);

        createPatient("hid2", "10", "11", "12", "nid1", "brn2", "uid2", "FName2", "SName2");
        UUID secondPatientCreateEventId = uuidForDate(new Date());
        createPatientUpdateLog(secondPatientCreateEventId, Constants.EVENT_TYPE_CREATED, "", "hid2");

        duplicatePatientFeedService.processDuplicatePatients();

        List<DuplicatePatient> duplicates = findAllDuplicates();
        assertEquals(2, duplicates.size());

        assertTrue(duplicateExist(duplicates, "hid2", "hid1", "A10B11", DUPLICATE_REASON_NID));
        assertTrue(duplicateExist(duplicates, "hid2", "hid1", "A10B11C12", DUPLICATE_REASON_NID));
        assertMarker(secondPatientCreateEventId.toString());
    }

    @Test
    public void shouldMarkPatientAsDuplicateAfterUpdated() throws Exception {
        createPatient("hid1", "10", "11", "12", "nid1", "brn1", "uid1", "FName1", "SName1");
        createPatient("hid2", "10", "11", "12", "nid2", "brn2", "uid2", "FName2", "SName2");
        UUID existingMarker = uuidForDate(new Date());
        saveMarker(existingMarker);

        UUID newMarker = uuidForDate(new Date());

        Patient patient1 = patientMapper.get("hid1");
        patient1.setNationalId("nid2");
        patientMapper.save(patient1);

        createPatientUpdateLog(newMarker, Constants.EVENT_TYPE_UPDATED, "{}", "hid1");

        duplicatePatientFeedService.processDuplicatePatients();

        List<DuplicatePatient> duplicates = findAllDuplicates();
        assertEquals(2, duplicates.size());

        assertTrue(duplicateExist(duplicates, "hid1", "hid2", "A10B11", DUPLICATE_REASON_NID));
        assertTrue(duplicateExist(duplicates, "hid1", "hid2", "A10B11C12", DUPLICATE_REASON_NID));
        assertMarker(newMarker.toString());
    }

    @Test
    public void shouldRetireDuplicatesWhenPatientIsRetired() throws Exception {
        createPatient("hid1", "10", "11", "12", "nid1", "brn1", "uid1", "fname1", "sname1");
        createPatient("hid2", "10", "11", "12", "nid1", "brn2", "uid2", "FName2", "SName2");
        UUID createEventId = uuidForDate(new Date());
        createPatientUpdateLog(createEventId, Constants.EVENT_TYPE_CREATED, "{}", "hid1");

        duplicatePatientMapper.save(new DuplicatePatient("A10B11", "hid1", "hid2", asSet(DUPLICATE_REASON_NID), uuidForDate(new Date())));
        duplicatePatientMapper.save(new DuplicatePatient("A10B11C12", "hid1", "hid2", asSet(DUPLICATE_REASON_NID), uuidForDate(new Date())));
        saveMarker(createEventId);

        Patient patient1 = patientMapper.get("hid1");
        patient1.setActive(false);
        patientMapper.save(patient1);

        UUID retiredEventId = uuidForDate(new Date());
        createPatientUpdateLog(retiredEventId, Constants.EVENT_TYPE_UPDATED, "{\"active\":{\"new_value\":false,\"old_value\":true}}", "hid1");

        duplicatePatientFeedService.processDuplicatePatients();

        List<DuplicatePatient> duplicates = findAllDuplicates();
        assertEquals(0, duplicates.size());

        assertMarker(retiredEventId.toString());
    }

    private boolean duplicateExist(List<DuplicatePatient> duplicatePatients, String healthId1,
                                   String healthId2, String catchmentId, String reason) {
        return duplicatePatients.stream().anyMatch(patient -> patient.getHealthId1().equals(healthId1) &&
                        patient.getHealthId2().equals(healthId2) &&
                        patient.getCatchmentId().equals(catchmentId) &&
                        patient.getReasons().contains(reason)
        );
    }

    private void createPatientUpdateLog(UUID eventId, String eventType, String changeSet, String healthId) {
        PatientUpdateLog updateLog = new PatientUpdateLog();
        updateLog.setEventId(eventId);
        updateLog.setHealthId(healthId);
        updateLog.setEventType(eventType);
        updateLog.setChangeSet(changeSet);
        updateLog.setRequestedBy(CREATED_BY);

        updateLogMapper.save(updateLog);
    }

    private void assertMarker(String marker) {
        Select select = select().from(CF_MARKER).where(eq(TYPE, DUPLICATE_PATIENT_MARKER)).limit(1);
        Row row = mappingManager.getSession().execute(select).one();
        assertEquals(marker, row.getString(MARKER));
    }

    private void createPatient(String healthId, String divisionId, String districtId, String upazilaId,
                               String nid, String brn, String uid, String givenName, String sName) {
        Patient patient = new Patient();
        patient.setHealthId(healthId);
        patient.setDivisionId(divisionId);
        patient.setDistrictId(districtId);
        patient.setUpazilaId(upazilaId);
        patient.setNationalId(nid);
        patient.setBirthRegistrationNumber(brn);
        patient.setUid(uid);
        patient.setGivenName(givenName);
        patient.setSurName(sName);
        patient.setActive(true);

        patientMapper.save(patient);
        nidMapper.save(new NidMapping(nid, healthId));
        brnMapper.save(new BrnMapping(brn, healthId));
        uidMapper.save(new UidMapping(uid, healthId));
        nameMapper.save(new NameMapping(divisionId, districtId, upazilaId, givenName, sName, healthId));
    }

    private List<DuplicatePatient> findAllDuplicates() {
        ResultSet resultSet = mappingManager.getSession().execute(select().from(CF_PATIENT_DUPLICATE));
        Result<DuplicatePatient> map = duplicatePatientMapper.map(resultSet);
        return map.all();
    }

    private void saveMarker(UUID marker) {
        Marker markerToSave = new Marker();
        markerToSave.setType(DUPLICATE_PATIENT_MARKER);
        markerToSave.setValue(marker.toString());
        markerToSave.setCreatedAt(uuidForDate(new Date()));
        markerMapper.save(markerToSave);
    }
}