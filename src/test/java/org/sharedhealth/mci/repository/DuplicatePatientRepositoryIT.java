package org.sharedhealth.mci.repository;

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
import org.sharedhealth.mci.model.Catchment;
import org.sharedhealth.mci.model.DuplicatePatient;
import org.sharedhealth.mci.model.Marker;
import org.sharedhealth.mci.model.Patient;
import org.sharedhealth.mci.util.TestUtils;
import org.sharedhealth.mci.util.TimeUuidUtil;

import java.util.*;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.*;
import static org.sharedhealth.mci.util.Constants.*;

public class DuplicatePatientRepositoryIT extends BaseIntegrationTest {
    private Mapper<DuplicatePatient> duplicatePatientMapper;
    private Mapper<Patient> patientMapper;
    private Mapper<Marker> markerMapper;
    private DuplicatePatientRepository duplicatePatientRepository;
    private MappingManager mappingManager;

    @Before
    public void setUp() throws Exception {
        mappingManager = MCICassandraConfig.getInstance().getMappingManager();
        duplicatePatientMapper = mappingManager.mapper(DuplicatePatient.class);
        patientMapper = mappingManager.mapper(Patient.class);
        markerMapper = mappingManager.mapper(Marker.class);
        duplicatePatientRepository = new DuplicatePatientRepository(mappingManager);
    }

    @After
    public void tearDown() throws Exception {
        TestUtils.truncateAllColumnFamilies();
    }

    @Test
    public void shouldCreateDuplicateAndUpdateMarker() {
        Catchment catchment = new Catchment("192939");
        String healthId1 = "111";
        String healthId2 = "110";
        Set<String> reasons = new HashSet<>(asList("nid"));

        UUID createdAt = TimeUuidUtil.uuidForDate(new Date());
        UUID marker = randomUUID();

        DuplicatePatient duplicate = new DuplicatePatient(catchment.getId(), healthId1, healthId2, reasons, createdAt);
        duplicatePatientRepository.create(asList(duplicate), marker);

        DuplicatePatient actualDuplicate = duplicatePatientMapper.get(catchment.getId(), createdAt);
        assertNotNull(actualDuplicate);
        assertEquals(catchment.getId(), actualDuplicate.getCatchmentId());
        assertEquals(healthId1, actualDuplicate.getHealthId1());
        assertEquals(healthId2, actualDuplicate.getHealthId2());
        assertEquals(reasons, actualDuplicate.getReasons());
        assertEquals(createdAt, actualDuplicate.getCreatedAt());

        assertMarker(marker.toString());
    }

    @Test
    public void shouldRetirePatientAndUpdateMarker() {
        String healthId1 = "h001";
        String healthId2 = "h002";
        String healthId3 = "h003";
        Patient patient1 = buildPatient(healthId1, "10", "11", "12");
        Patient patient2 = buildPatient(healthId2, "20", "21", "22");
        Patient patient3 = buildPatient(healthId3, "30", "31", "32");
        patientMapper.save(patient1);
        patientMapper.save(patient2);
        patientMapper.save(patient3);
        Set<String> reasons = new HashSet<>(asList("nid"));

        createDuplicatesForPatient(healthId1, healthId2, patient1.getCatchment(), reasons);
        createDuplicatesForPatient(healthId2, healthId1, patient2.getCatchment(), reasons);
        createDuplicatesForPatient(healthId2, healthId3, patient2.getCatchment(), reasons);
        createDuplicatesForPatient(healthId3, healthId2, patient3.getCatchment(), reasons);
        createDuplicatesForPatient(healthId1, healthId3, patient1.getCatchment(), reasons);
        createDuplicatesForPatient(healthId3, healthId1, patient3.getCatchment(), reasons);

        UUID createMarker = randomUUID();
        saveMarker(createMarker);

        UUID retireMarker = randomUUID();
        duplicatePatientRepository.retire(healthId1, retireMarker);

        List<DuplicatePatient> duplicates = findAllDuplicates();
        assertFalse(duplicates.isEmpty());
        assertEquals(4, duplicates.size());

        for (String catchmentId : patient2.getCatchment().getAllIds()) {
            assertTrue(duplicateExist(duplicates, healthId2, healthId3, catchmentId));
        }
        for (String catchmentId : patient3.getCatchment().getAllIds()) {
            assertTrue(duplicateExist(duplicates, healthId3, healthId2, catchmentId));
        }
        assertMarker(retireMarker.toString());
    }

    private void createDuplicatesForPatient(String healthId1, String healthId2, Catchment catchment, Set<String> reasons) {
        for (String catchmentId : catchment.getAllIds()) {
            DuplicatePatient duplicatePatient = new DuplicatePatient(catchmentId, healthId1, healthId2, reasons, TimeUuidUtil.uuidForDate(new Date()));
            duplicatePatientMapper.save(duplicatePatient);
        }
    }


    private boolean duplicateExist(List<DuplicatePatient> duplicatePatients, String healthId1, String healthId2, String catchmentId) {
        return duplicatePatients.stream().anyMatch(patient -> patient.getHealthId1().equals(healthId1) &&
                patient.getHealthId2().equals(healthId2) &&
                patient.getCatchmentId().equals(catchmentId));
    }

    private void assertMarker(String marker) {
        Select select = select().from(CF_MARKER).where(eq(TYPE, DUPLICATE_PATIENT_MARKER)).limit(1);
        Row row = mappingManager.getSession().execute(select).one();
        assertEquals(marker, row.getString(MARKER));
    }

    private Patient buildPatient(String healthId, String divisionId, String districtId, String upazilaId) {
        Patient patient = new Patient();
        patient.setHealthId(healthId);
        patient.setDivisionId(divisionId);
        patient.setDistrictId(districtId);
        patient.setUpazilaId(upazilaId);
        return patient;
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
        markerToSave.setCreatedAt(TimeUuidUtil.uuidForDate(new Date()));
        markerMapper.save(markerToSave);
    }


//
//    @Test
//    public void shouldUpdatePatientAndUpdateMarker() {
//        PatientData patient1 = buildPatient("h001", new Address("10", "11", "12"));
//        String healthId1 = patientRepository.create(patient1).getId();
//        PatientData patient2 = buildPatient("h002", new Address("20", "21", "22"));
//        String healthId2 = patientRepository.create(patient2).getId();
//        PatientData patient3 = buildPatient("h003", new Address("30", "31", "32"));
//        String healthId3 = patientRepository.create(patient3).getId();
//        PatientData patient4 = buildPatient("h004", new Address("40", "41", "42"));
//        String healthId4 = patientRepository.create(patient4).getId();
//        Set<String> reasons = asSet("nid");
//
//        duplicatePatientRepository.create(asList(
//                        new DuplicatePatient(patient1.getCatchment().getId(), healthId1, healthId2, reasons, TimeUuidUtil.uuidForDate(new Date())),
//                        new DuplicatePatient(patient2.getCatchment().getId(), healthId2, healthId1, reasons, TimeUuidUtil.uuidForDate(new Date()))),
//                randomUUID());
//
//        cassandraOps.insert(new DuplicatePatientIgnored(healthId1, healthId4, reasons));
//        cassandraOps.insert(new DuplicatePatientIgnored(healthId4, healthId1, reasons));
//
//        UUID marker = randomUUID();
//        duplicatePatientRepository.update(healthId1, new Catchment("101112"), asList(
//                new DuplicatePatient(patient1.getCatchment().getId(), healthId1, healthId3, reasons, TimeUuidUtil.uuidForDate(new Date())),
//                new DuplicatePatient(patient3.getCatchment().getId(), healthId3, healthId1, reasons, TimeUuidUtil.uuidForDate(new Date())),
//                new DuplicatePatient(patient1.getCatchment().getId(), healthId1, healthId4, reasons, TimeUuidUtil.uuidForDate(new Date())),
//                new DuplicatePatient(patient4.getCatchment().getId(), healthId4, healthId1, reasons, TimeUuidUtil.uuidForDate(new Date()))
//        ), marker);
//
//        List<DuplicatePatient> duplicates = findAllDuplicates();
//        assertTrue(isNotEmpty(duplicates));
//        assertEquals(2, duplicates.size());
//        duplicateExist(healthId1, healthId3, patient1.getCatchment().getId(), duplicates.get(0));
//        duplicateExist(healthId3, healthId1, patient3.getCatchment().getId(), duplicates.get(1));
//        assertMarker(marker);
//    }
}