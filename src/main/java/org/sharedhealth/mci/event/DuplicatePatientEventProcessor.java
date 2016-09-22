package org.sharedhealth.mci.event;


import org.sharedhealth.mci.model.Catchment;
import org.sharedhealth.mci.model.DuplicatePatient;
import org.sharedhealth.mci.model.PatientUpdateLog;
import org.sharedhealth.mci.repository.DuplicatePatientRepository;
import org.sharedhealth.mci.rule.DuplicatePatientRuleEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class DuplicatePatientEventProcessor {

    private static final String ALPHABETS = "[A-Za-z]";

    private DuplicatePatientRuleEngine ruleEngine;
    protected DuplicatePatientRepository duplicatePatientRepository;

    public DuplicatePatientEventProcessor(DuplicatePatientRuleEngine ruleEngine, DuplicatePatientRepository duplicatePatientRepository) {
        this.ruleEngine = ruleEngine;
        this.duplicatePatientRepository = duplicatePatientRepository;
    }

    public abstract void process(PatientUpdateLog log, UUID marker);

    public List<DuplicatePatient> buildDuplicates(String healthId) {
        return ruleEngine.apply(healthId);
    }

    protected List<DuplicatePatient> filterPersistentDuplicates(List<DuplicatePatient> duplicates) {
        List<DuplicatePatient> filteredList = new ArrayList<>();
        filteredList.addAll(duplicates);
        for (DuplicatePatient duplicatepatient : duplicates) {
            List<DuplicatePatient> persistentEntries = duplicatePatientRepository.findByCatchmentAndHealthId(new Catchment(formatCatchmentId(duplicatepatient.getCatchmentId())), duplicatepatient.getHealthId2());
            persistentEntries.addAll(duplicatePatientRepository.findByCatchmentAndHealthId(new Catchment(formatCatchmentId(duplicatepatient.getCatchmentId())), duplicatepatient.getHealthId1()));
            persistentEntries.stream().filter(persistentEntry -> isSameEntry(persistentEntry, duplicatepatient)).forEach(persistentEntry -> filteredList.remove(duplicatepatient));
        }
        return filteredList;
    }

    private String formatCatchmentId(String catchmentId) {
        return catchmentId.replaceAll(ALPHABETS, "");
    }

    private boolean isSameEntry(DuplicatePatient entry1, DuplicatePatient entry2) {
        if (entry1.getHealthId2().equals(entry2.getHealthId1()) && entry1.getHealthId1().equals(entry2.getHealthId2()) && entry1.getReasons().equals(entry2.getReasons())) {
            return true;
        }
        if (entry1.getHealthId1().equals(entry2.getHealthId1()) && entry1.getHealthId2().equals(entry2.getHealthId2()) && entry1.getReasons().equals(entry2.getReasons())) {
            return true;
        }
        return false;
    }
}
