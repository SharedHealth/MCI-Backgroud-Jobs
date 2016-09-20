package org.sharedhealth.mci.model;


import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.util.Set;
import java.util.UUID;

import static org.sharedhealth.mci.util.Constants.*;

@Table(name = CF_PATIENT_DUPLICATE)
public class DuplicatePatient {

    @Column(name = CATCHMENT_ID)
    @PartitionKey
    private String catchmentId;

    @Column(name = CREATED_AT)
    @PartitionKey(1)
    private UUID createdAt;

    @Column(name = HEALTH_ID1)
    private String healthId1;

    @Column(name = HEALTH_ID2)
    private String healthId2;

    @Column(name = REASONS)
    private Set<String> reasons;

    public DuplicatePatient() {
    }

    public DuplicatePatient(String catchment_id, String health_id1, String health_id2) {
        this(catchment_id, health_id1, health_id2, null, null);
    }

    public DuplicatePatient(String catchmentId, String healthId1, String healthId2, Set<String> reasons, UUID createdAt) {
        this.catchmentId = catchmentId;
        this.healthId1 = healthId1;
        this.healthId2 = healthId2;
        this.reasons = reasons;
        this.createdAt = createdAt;
    }

    public String getCatchmentId() {
        return catchmentId;
    }

    public void setCatchmentId(String catchmentId) {
        this.catchmentId = catchmentId;
    }

    public UUID getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(UUID createdAt) {
        this.createdAt = createdAt;
    }

    public String getHealthId1() {
        return healthId1;
    }

    public void setHealthId1(String healthId1) {
        this.healthId1 = healthId1;
    }

    public String getHealthId2() {
        return healthId2;
    }

    public void setHealthId2(String healthId2) {
        this.healthId2 = healthId2;
    }

    public Set<String> getReasons() {
        return reasons;
    }

    public void setReasons(Set<String> reasons) {
        this.reasons = reasons;
    }

    public void addReason(String reason) {
        this.reasons.add(reason);
    }
}
