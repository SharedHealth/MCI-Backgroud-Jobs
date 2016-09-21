package org.sharedhealth.mci.rule;


import org.sharedhealth.mci.model.Catchment;
import org.sharedhealth.mci.model.DuplicatePatient;
import org.sharedhealth.mci.model.Patient;
import org.sharedhealth.mci.repository.PatientRepository;
import org.sharedhealth.mci.util.TimeUuidUtil;

import java.util.*;

import static java.util.Arrays.asList;

public abstract class DuplicatePatientRule {

    public static final String DUPLICATE_REASON_NID = "DUPLICATE_REASON_NID";
    public static final String DUPLICATE_REASON_UID = "DUPLICATE_REASON_UID";
    public static final String DUPLICATE_REASON_BRN = "DUPLICATE_REASON_BRN";
    public static final String DUPLICATE_REASON_NAME_ADDRESS = "DUPLICATE_REASON_NAME_ADDRESS";

    protected PatientRepository patientRepository;

    protected DuplicatePatientRule(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public void apply(String healthId, List<DuplicatePatient> duplicates) {
        Patient patient = patientRepository.findByHealthId(healthId);
        List<Patient> patients = buildSearchQuery(patient);
        List<String> healthIds = findDuplicatesBySearchQuery(healthId, patients);
        buildDuplicates(patient, healthIds, getReason(), duplicates);
    }

    protected abstract List<Patient> buildSearchQuery(Patient patient);

    protected abstract String getReason();

    protected List<String> findDuplicatesBySearchQuery(String healthId, List<Patient> duplicatePatients) {
        List<String> duplicateHealthIds = new ArrayList<>();
        duplicatePatients.forEach(patient -> {
            if (!healthId.equals(patient.getHealthId()) && patient.isActive()) {
                duplicateHealthIds.add(patient.getHealthId());
            }
        });
        return duplicateHealthIds;
    }

    protected void buildDuplicates(final Patient patient1, List<String> healthIds, String reason,
                                   List<DuplicatePatient> duplicates) {
        for (final String healthId : healthIds) {
            DuplicatePatient duplicate = duplicates.stream().filter(d -> patient1.getHealthId().equals(d.getHealthId1())
                    && healthId.equals(d.getHealthId2())).findFirst().get();

            if (duplicate != null) {
                duplicate.addReason(reason);
            } else {
                Patient patient2 = patientRepository.findByHealthId(healthId);
                HashSet<String> reasons = new HashSet<>(asList(reason));
                List<DuplicatePatient> duplicatesWithRule = mapToDuplicatePatientData(patient1, patient2, reasons);
                duplicates.addAll(duplicatesWithRule);
            }
        }
    }

    private List<DuplicatePatient> mapToDuplicatePatientData(Patient patient1, Patient patient2, Set<String> reasons) {
        List<DuplicatePatient> duplicates = new ArrayList<>();
        Catchment catchment1 = patient1.getCatchment();
        Catchment catchment2 = patient2.getCatchment();
        if (catchment1.equals(catchment2)) {
            buildDuplicates(catchment1, patient1.getHealthId(), patient2.getHealthId(), reasons, duplicates);
        } else {
            buildDuplicates(catchment1, patient1.getHealthId(), patient2.getHealthId(), reasons, duplicates);
            buildDuplicates(catchment2, patient2.getHealthId(), patient1.getHealthId(), reasons, duplicates);
        }
        return duplicates;
    }

    private void buildDuplicates(Catchment catchment, String healthId1, String healthId2, Set<String> reasons,
                                 List<DuplicatePatient> duplicates) {
        for (String catchmentId : catchment.getAllIds()) {
            DuplicatePatient duplicate = new DuplicatePatient(catchmentId, healthId1, healthId2, reasons, TimeUuidUtil.uuidForDate(new Date()));
            duplicates.add(duplicate);
        }
    }
}
