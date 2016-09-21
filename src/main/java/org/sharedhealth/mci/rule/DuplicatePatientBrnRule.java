package org.sharedhealth.mci.rule;

import org.apache.commons.lang3.StringUtils;
import org.sharedhealth.mci.model.Patient;
import org.sharedhealth.mci.repository.PatientRepository;

import java.util.ArrayList;
import java.util.List;

import static org.sharedhealth.mci.util.Constants.BIN_BRN;
import static org.sharedhealth.mci.util.Constants.CF_BRN_MAPPING;

public class DuplicatePatientBrnRule extends DuplicatePatientRule {

    private final String reason;

    public DuplicatePatientBrnRule(PatientRepository patientRepository) {
        super(patientRepository);
        this.reason = DUPLICATE_REASON_BRN;
    }

    @Override
    protected List<Patient> buildSearchQuery(Patient patient) {
        String brn = patient.getBirthRegistrationNumber();
        if (StringUtils.isBlank(brn)) return new ArrayList<>();
        return patientRepository.findAllByQuery(BIN_BRN, brn, CF_BRN_MAPPING);
    }

    @Override
    protected String getReason() {
        return reason;
    }
}
