package org.sharedhealth.mci.model;


import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import static org.sharedhealth.mci.util.Constants.*;

@Table(name = CF_NID_MAPPING)
public class NidMapping {

    @Column(name = NATIONAL_ID)
    @PartitionKey
    private String nationalId;

    @Column(name = HEALTH_ID)
    @PartitionKey(value = 1)
    private String healthId;

    public NidMapping() {
    }

    public NidMapping(String nationalId, String healthId) {
        this.nationalId = nationalId;
        this.healthId = healthId;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getHealthId() {
        return healthId;
    }

    public void setHealthId(String healthId) {
        this.healthId = healthId;
    }
}