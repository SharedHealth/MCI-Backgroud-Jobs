package org.sharedhealth.mci.model;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.sharedhealth.mci.util.DateUtil;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static org.sharedhealth.mci.util.Constants.*;

@Table(name = CF_PATIENT_UPDATE_LOG)
public class PatientUpdateLog {

    @PartitionKey()
    @Column(name = YEAR)
    private int year;

    @Column(name = EVENT_ID)
    @PartitionKey(value = 1)
    private UUID eventId;

    @Column(name = HEALTH_ID)
    @PartitionKey(value = 2)
    private String healthId;

    @Column(name = CHANGE_SET)
    private String changeSet;

    @Column(name = REQUESTED_BY)
    private String requestedBy;

    @Column(name = APPROVED_BY)
    private String approvedBy;

    @Column(name = EVENT_TYPE)
    private String eventType;

    public String getHealthId() {
        return healthId;
    }

    public void setHealthId(String healthId) {
        this.healthId = healthId;
    }

    public String getChangeSet() {
        return changeSet;
    }

    public void setChangeSet(String changeSet) {
        this.changeSet = changeSet;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
        this.year = DateUtil.getYearOf(eventId);
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Catchment getOldCatchmentFromChangeSet() {
        Object oldValue = getOldValueFromChangeSet(PRESENT_ADDRESS);
        if (oldValue != null) {
            return new Catchment((Map<String, String>) oldValue);
        }
        return null;
    }

    public Object getOldValueFromChangeSet(String key) {
        Map<String, Map<String, Object>> changeSetMap = buildChangeSet(changeSet);
        if (changeSetMap == null) return null;

        Map<String, Object> changeSetForKey = changeSetMap.get(key);
        return changeSetForKey == null ? null : changeSetForKey.get(OLD_VALUE);
    }

    private Map<String, Map<String, Object>> buildChangeSet(String changeSet) {
        if (changeSet == null) {
            return null;
        }
        try {
            return new ObjectMapper().readValue(changeSet, new TypeReference<Map<String, Map<String, Object>>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException("Improper changset : " + changeSet);
        }
    }


}
