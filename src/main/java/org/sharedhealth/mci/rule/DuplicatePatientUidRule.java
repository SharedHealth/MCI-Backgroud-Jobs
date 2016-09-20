package org.sharedhealth.mci.rule;

import org.apache.commons.lang3.StringUtils;
import org.sharedhealth.mci.mapper.DuplicatePatientMapper;
import org.sharedhealth.mci.model.Patient;
import org.sharedhealth.mci.repository.PatientRepository;

import java.util.ArrayList;
import java.util.List;

import static org.sharedhealth.mci.util.Constants.CF_UID_MAPPING;
import static org.sharedhealth.mci.util.Constants.UID;

public class DuplicatePatientUidRule extends DuplicatePatientRule {

    private final String reason;

    public DuplicatePatientUidRule(PatientRepository patientRepository, DuplicatePatientMapper duplicatePatientMapper) {
        super(patientRepository, duplicatePatientMapper);
        this.reason = DUPLICATE_REASON_UID;
    }

    @Override
    protected List<Patient> buildSearchQuery(Patient patient) {
        String uid = patient.getUid();
        if (StringUtils.isBlank(uid)) return new ArrayList<>();
        return patientRepository.findAllByQuery(UID, uid, CF_UID_MAPPING);
    }

    @Override
    protected String getReason() {
        return reason;
    }
}
