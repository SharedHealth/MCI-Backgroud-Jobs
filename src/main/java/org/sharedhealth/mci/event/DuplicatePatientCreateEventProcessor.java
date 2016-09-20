package org.sharedhealth.mci.event;


import org.sharedhealth.mci.model.DuplicatePatient;
import org.sharedhealth.mci.model.PatientUpdateLog;
import org.sharedhealth.mci.repository.DuplicatePatientRepository;
import org.sharedhealth.mci.rule.DuplicatePatientRuleEngine;

import java.util.List;
import java.util.UUID;

public class DuplicatePatientCreateEventProcessor extends DuplicatePatientEventProcessor {

    public DuplicatePatientCreateEventProcessor(DuplicatePatientRuleEngine ruleEngine, DuplicatePatientRepository duplicatePatientRepository) {
        super(ruleEngine, duplicatePatientRepository);
    }

    @Override
    public void process(PatientUpdateLog log, UUID marker) {
        List<DuplicatePatient> duplicates = buildDuplicates(log.getHealthId());
        duplicates = filterPersistentDuplicates(duplicates);
        duplicatePatientRepository.create(duplicates, marker);
    }


}
