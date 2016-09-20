package org.sharedhealth.mci.rule;


import org.sharedhealth.mci.mapper.DuplicatePatientMapper;
import org.sharedhealth.mci.model.Patient;
import org.sharedhealth.mci.repository.PatientRepository;

import java.util.List;

public class DuplicatePatientNameAndAddressRule extends DuplicatePatientRule {

    private final String reason;

    public DuplicatePatientNameAndAddressRule(PatientRepository patientRepository, DuplicatePatientMapper duplicatePatientMapper) {
        super(patientRepository, duplicatePatientMapper);
        this.reason = DUPLICATE_REASON_NAME_ADDRESS;
    }

    @Override
    protected List<Patient> buildSearchQuery(Patient patient) {
        return patientRepository.findAllByNameAndAddress(patient.getGivenName(), patient.getSurName(),
                patient.getDivisionId(), patient.getDistrictId(), patient.getUpazilaId());
    }

    @Override
    protected String getReason() {
        return reason;
    }
}
