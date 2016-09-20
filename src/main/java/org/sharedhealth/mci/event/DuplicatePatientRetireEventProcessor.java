package org.sharedhealth.mci.event;


import org.sharedhealth.mci.model.PatientUpdateLog;
import org.sharedhealth.mci.repository.DuplicatePatientRepository;
import org.sharedhealth.mci.rule.DuplicatePatientRuleEngine;

import java.util.UUID;

public class DuplicatePatientRetireEventProcessor extends DuplicatePatientEventProcessor {

    public DuplicatePatientRetireEventProcessor(DuplicatePatientRuleEngine ruleEngine, DuplicatePatientRepository duplicatePatientRepository) {
        super(ruleEngine, duplicatePatientRepository);
    }

    @Override
    public void process(PatientUpdateLog log, UUID marker) {
        duplicatePatientRepository.retire(log.getHealthId(), marker);
    }
}
