package org.sharedhealth.mci.event;


import org.sharedhealth.mci.model.DuplicatePatient;
import org.sharedhealth.mci.model.PatientUpdateLog;
import org.sharedhealth.mci.repository.DuplicatePatientRepository;
import org.sharedhealth.mci.rule.DuplicatePatientRuleEngine;

import java.util.List;
import java.util.UUID;

public class DuplicatePatientUpdateEventProcessor extends DuplicatePatientEventProcessor {

    public DuplicatePatientUpdateEventProcessor(DuplicatePatientRuleEngine ruleEngine, DuplicatePatientRepository duplicatePatientRepository) {
        super(ruleEngine, duplicatePatientRepository);
    }

    @Override
    public void process(PatientUpdateLog log, UUID marker) {
        String healthId = log.getHealthId();
        List<DuplicatePatient> duplicates = buildDuplicates(healthId);
        duplicatePatientRepository.update(healthId, log.getOldCatchmentFromChangeSet(), duplicates, marker);
    }
}
