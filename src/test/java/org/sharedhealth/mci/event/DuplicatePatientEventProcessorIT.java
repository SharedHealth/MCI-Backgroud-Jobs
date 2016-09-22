package org.sharedhealth.mci.event;

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
import org.sharedhealth.mci.model.*;
import org.sharedhealth.mci.repository.DuplicatePatientRepository;
import org.sharedhealth.mci.repository.PatientRepository;
import org.sharedhealth.mci.rule.*;
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
import static org.sharedhealth.mci.rule.DuplicatePatientRule.*;
import static org.sharedhealth.mci.util.Constants.*;
import static org.sharedhealth.mci.util.TimeUuidUtil.uuidForDate;


public class DuplicatePatientEventProcessorIT extends BaseIntegrationTest {
    private static final String CREATED_BY = "{\"facility\":\"Bahmni\",\"provider\":\"Dr. Monika\"";

    private DuplicatePatientEventProcessor eventProcessor;
    private MappingManager mappingManager;
    private DuplicatePatientRuleEngine ruleEngine;
    private DuplicatePatientRepository duplicateRepository;

    private Mapper<Patient> patientMapper;
    private Mapper<DuplicatePatient> duplicatePatientMapper;
    private Mapper<NidMapping> nidMapper;
    private Mapper<BrnMapping> brnMapper;
    private Mapper<UidMapping> uidMapper;
    private Mapper<NameMapping> nameMapper;
    private Mapper<Marker> markerMapper;

    @Before
    public void setUp() throws Exception {
        mappingManager = MCICassandraConfig.getInstance().getMappingManager();
        PatientRepository patientRepository = new PatientRepository(mappingManager);

        DuplicatePatientBrnRule brnRule = new DuplicatePatientBrnRule(patientRepository);
        DuplicatePatientNidRule nidRule = new DuplicatePatientNidRule(patientRepository);
        DuplicatePatientUidRule uidRule = new DuplicatePatientUidRule(patientRepository);
        DuplicatePatientNameAndAddressRule nameAndAddressRule = new DuplicatePatientNameAndAddressRule(patientRepository);
        ruleEngine = new DuplicatePatientRuleEngine(asList(brnRule, nidRule, uidRule, nameAndAddressRule));
        duplicateRepository = new DuplicatePatientRepository(mappingManager, patientRepository);

        patientMapper = mappingManager.mapper(Patient.class);
        duplicatePatientMapper = mappingManager.mapper(DuplicatePatient.class);
        nidMapper = mappingManager.mapper(NidMapping.class);
        brnMapper = mappingManager.mapper(BrnMapping.class);
        uidMapper = mappingManager.mapper(UidMapping.class);
        nameMapper = mappingManager.mapper(NameMapping.class);
        markerMapper = mappingManager.mapper(Marker.class);
    }

    @After
    public void tearDown() throws Exception {
        TestUtils.truncateAllColumnFamilies();
    }

    @Test
    public void shouldCreateTheDuplicatesInSameCatchment() throws Exception {
        createPatient("hid1", "10", "11", "12", "nid1", "brn1", "uid1", "FName1", "SName1");
        createPatient("hid2", "10", "11", "12", "nid1", "brn2", "uid2", "FName2", "SName2");

        eventProcessor = new DuplicatePatientCreateEventProcessor(ruleEngine, duplicateRepository);
        UUID eventId = uuidForDate(new Date());
        PatientUpdateLog log = buildPatientUpdateLog(eventId, EVENT_TYPE_CREATED, "", "hid1");

        eventProcessor.process(log, eventId);

        List<DuplicatePatient> duplicates = findAllDuplicates();
        assertEquals(2, duplicates.size());

        assertTrue(duplicateExist(duplicates, "hid1", "hid2", "A10B11", DUPLICATE_REASON_NID));
        assertTrue(duplicateExist(duplicates, "hid1", "hid2", "A10B11C12", DUPLICATE_REASON_NID));
        assertMarker(eventId.toString());
    }

    @Test
    public void shouldCreateTheDuplicatesInDifferentCatchment() throws Exception {
        createPatient("hid1", "10", "11", "12", "nid1", "brn1", "uid1", "FName1", "SName1");
        createPatient("hid2", "30", "11", "12", "nid1", "brn2", "uid2", "FName2", "SName2");

        eventProcessor = new DuplicatePatientCreateEventProcessor(ruleEngine, duplicateRepository);
        UUID eventId = uuidForDate(new Date());
        PatientUpdateLog log = buildPatientUpdateLog(eventId, EVENT_TYPE_CREATED, "", "hid1");

        eventProcessor.process(log, eventId);

        List<DuplicatePatient> duplicates = findAllDuplicates();
        assertEquals(4, duplicates.size());

        assertTrue(duplicateExist(duplicates, "hid1", "hid2", "A10B11", DUPLICATE_REASON_NID));
        assertTrue(duplicateExist(duplicates, "hid1", "hid2", "A10B11C12", DUPLICATE_REASON_NID));
        assertTrue(duplicateExist(duplicates, "hid2", "hid1", "A30B11", DUPLICATE_REASON_NID));
        assertTrue(duplicateExist(duplicates, "hid2", "hid1", "A30B11C12", DUPLICATE_REASON_NID));
        assertMarker(eventId.toString());
    }

    @Test
    public void shouldCreateTheDuplicatesMatchingWithMultipleRules() throws Exception {
        createPatient("hid1", "10", "11", "12", "nid1", "brn1", "uid1", "fname1", "sname1");
        createPatient("hid2", "10", "11", "12", "nid1", "brn2", "uid2", "FName2", "SName2");
        createPatient("hid3", "10", "11", "12", "nid2", "brn1", "uid3", "FName4", "SName4");
        createPatient("hid4", "10", "11", "12", "nid3", "brn3", "uid1", "FName3", "SName3");
        createPatient("hid5", "10", "11", "12", "nid3", "brn3", "uid3", "fname1", "sname1");

        eventProcessor = new DuplicatePatientCreateEventProcessor(ruleEngine, duplicateRepository);
        UUID eventId = uuidForDate(new Date());
        PatientUpdateLog log = buildPatientUpdateLog(eventId, EVENT_TYPE_CREATED, "", "hid1");

        eventProcessor.process(log, eventId);

        List<DuplicatePatient> duplicates = findAllDuplicates();
        assertEquals(8, duplicates.size());

        for (String catchmentId : new Catchment("101112").getAllIds()) {
            assertTrue(duplicateExist(duplicates, "hid1", "hid2", catchmentId, DUPLICATE_REASON_NID));
            assertTrue(duplicateExist(duplicates, "hid1", "hid3", catchmentId, DUPLICATE_REASON_BRN));
            assertTrue(duplicateExist(duplicates, "hid1", "hid4", catchmentId, DUPLICATE_REASON_UID));
            assertTrue(duplicateExist(duplicates, "hid1", "hid5", catchmentId, DUPLICATE_REASON_NAME_ADDRESS));
        }
        assertMarker(eventId.toString());
    }

    @Test
    public void shouldUpdateTheDuplicatePatients() throws Exception {
        createPatient("hid1", "10", "11", "12", "nid1", "brn1", "uid1", "fname1", "sname1");
        createPatient("hid2", "10", "11", "12", "nid1", "brn2", "uid2", "FName2", "SName2");
        createPatient("hid3", "10", "11", "12", "nid2", "brn3", "uid3", "FName4", "SName4");
        UUID createEventId = uuidForDate(new Date());

        duplicatePatientMapper.save(new DuplicatePatient("A10B11", "hid1", "hid2", asSet(DUPLICATE_REASON_NID), uuidForDate(new Date())));
        duplicatePatientMapper.save(new DuplicatePatient("A10B11C12", "hid1", "hid2", asSet(DUPLICATE_REASON_NID), uuidForDate(new Date())));
        saveMarker(createEventId);

        Patient patient1 = patientMapper.get("hid1");
        patient1.setBirthRegistrationNumber("brn3");
        patientMapper.save(patient1);

        UUID updateEventId = uuidForDate(new Date());
        eventProcessor = new DuplicatePatientUpdateEventProcessor(ruleEngine, duplicateRepository);
        PatientUpdateLog log = buildPatientUpdateLog(updateEventId, EVENT_TYPE_UPDATED, "{}", "hid1");

        eventProcessor.process(log, updateEventId);

        List<DuplicatePatient> duplicates = findAllDuplicates();
        assertEquals(4, duplicates.size());

        for (String catchmentId : new Catchment("101112").getAllIds()) {
            assertTrue(duplicateExist(duplicates, "hid1", "hid2", catchmentId, DUPLICATE_REASON_NID));
            assertTrue(duplicateExist(duplicates, "hid1", "hid3", catchmentId, DUPLICATE_REASON_BRN));
        }
        assertMarker(updateEventId.toString());
    }

    @Test
    public void shouldRetireTheDuplicatePatients() throws Exception {
        createPatient("hid1", "10", "11", "12", "nid1", "brn1", "uid1", "fname1", "sname1");
        createPatient("hid2", "10", "11", "12", "nid1", "brn2", "uid2", "FName2", "SName2");
        UUID createEventId = uuidForDate(new Date());

        duplicatePatientMapper.save(new DuplicatePatient("A10B11", "hid1", "hid2", asSet(DUPLICATE_REASON_NID), uuidForDate(new Date())));
        duplicatePatientMapper.save(new DuplicatePatient("A10B11C12", "hid1", "hid2", asSet(DUPLICATE_REASON_NID), uuidForDate(new Date())));
        saveMarker(createEventId);

        Patient patient1 = patientMapper.get("hid1");
        patient1.setActive(false);
        patientMapper.save(patient1);

        UUID retiredEventId = uuidForDate(new Date());
        eventProcessor = new DuplicatePatientRetireEventProcessor(ruleEngine, duplicateRepository);
        PatientUpdateLog log = buildPatientUpdateLog(retiredEventId, EVENT_TYPE_UPDATED, "{}", "hid1");

        eventProcessor.process(log, retiredEventId);

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

    private PatientUpdateLog buildPatientUpdateLog(UUID eventId, String eventType, String changeSet, String healthId) {
        PatientUpdateLog updateLog = new PatientUpdateLog();
        updateLog.setEventId(eventId);
        updateLog.setHealthId(healthId);
        updateLog.setEventType(eventType);
        updateLog.setChangeSet(changeSet);
        updateLog.setRequestedBy(CREATED_BY);
        return updateLog;
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