package org.sharedhealth.mci.model;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import static org.sharedhealth.mci.util.Constants.*;

@Table(name = CF_UID_MAPPING)
public class UidMapping {

    @Column(name = UID)
    @PartitionKey
    private String uid;

    @Column(name = HEALTH_ID)
    @PartitionKey(value = 1)
    private String healthId;

    public UidMapping() {
    }

    public UidMapping(String uid, String healthId) {
        this.uid = uid;
        this.healthId = healthId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getHealthId() {
        return healthId;
    }

    public void setHealthId(String healthId) {
        this.healthId = healthId;
    }
}