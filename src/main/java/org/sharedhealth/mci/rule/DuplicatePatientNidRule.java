package org.sharedhealth.mci.rule;

import org.apache.commons.lang3.StringUtils;
import org.sharedhealth.mci.model.Patient;
import org.sharedhealth.mci.repository.PatientRepository;

import java.util.ArrayList;
import java.util.List;

import static org.sharedhealth.mci.util.Constants.CF_NID_MAPPING;
import static org.sharedhealth.mci.util.Constants.NATIONAL_ID;

public class DuplicatePatientNidRule extends DuplicatePatientRule {

    private final String reason;

    public DuplicatePatientNidRule(PatientRepository patientRepository) {
        super(patientRepository);
        this.reason = DUPLICATE_REASON_NID;
    }

    @Override
    protected List<Patient> findMatchingPatients(Patient patient) {
        String nationalId = patient.getNationalId();
        if (StringUtils.isBlank(nationalId)) return new ArrayList<>();
        return patientRepository.findAllByQuery(NATIONAL_ID, nationalId, CF_NID_MAPPING);
    }

    @Override
    protected String getReason() {
        return reason;
    }
}
