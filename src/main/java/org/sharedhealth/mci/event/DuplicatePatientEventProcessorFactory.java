package org.sharedhealth.mci.event;


import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.sharedhealth.mci.util.Constants.*;

public class DuplicatePatientEventProcessorFactory {

    private DuplicatePatientEventProcessor createEventProcessor;
    private DuplicatePatientEventProcessor updateEventProcessor;
    private DuplicatePatientEventProcessor retireEventProcessor;

    public DuplicatePatientEventProcessorFactory(DuplicatePatientCreateEventProcessor createEventProcessor,
                                                 DuplicatePatientUpdateEventProcessor updateEventProcessor,
                                                 DuplicatePatientRetireEventProcessor retireEventProcessor) {
        this.createEventProcessor = createEventProcessor;
        this.updateEventProcessor = updateEventProcessor;
        this.retireEventProcessor = retireEventProcessor;
    }

    public DuplicatePatientEventProcessor getEventProcessor(String eventType, String changeSet) throws IOException {
        if (EVENT_TYPE_CREATED.equals(eventType)) return createEventProcessor;

        if (EVENT_TYPE_UPDATED.equals(eventType)) {
            Map<String, Map<String, Object>> changeSetMap = new ObjectMapper().readValue(changeSet,
                    new TypeReference<Map<String, Map<String, Object>>>() {
                    });
            Map<String, Object> activeField = changeSetMap.get(ACTIVE);
            if (isActiveFieldRetired(activeField)) {
                return retireEventProcessor;
            }
            return updateEventProcessor;
        }
        return null;
    }

    public boolean isActiveFieldRetired(Map<String, Object> activeField) {
        if (activeField == null) return false;
        Object newValue = activeField.get(NEW_VALUE);
        Object oldValue = activeField.get(OLD_VALUE);
        if (newValue instanceof Boolean && oldValue instanceof Boolean) {
            return FALSE.equals(newValue) & TRUE.equals(oldValue);
        }
        return false;
    }
}
