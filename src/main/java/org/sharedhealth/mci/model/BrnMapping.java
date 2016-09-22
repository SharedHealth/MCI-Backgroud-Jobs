package org.sharedhealth.mci.model;


import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import static org.sharedhealth.mci.util.Constants.*;

@Table(name = CF_BRN_MAPPING)
public class BrnMapping {

    @Column(name = BIN_BRN)
    @PartitionKey
    private String binBrn;

    @Column(name = HEALTH_ID)
    @PartitionKey(value = 1)
    private String healthId;

    public BrnMapping() {
    }

    public BrnMapping(String brn, String healthId) {
        this.binBrn = brn;
        this.healthId = healthId;
    }

    public String getBinBrn() {
        return binBrn;
    }

    public void setBinBrn(String binBrn) {
        this.binBrn = binBrn;
    }

    public String getHealthId() {
        return healthId;
    }

    public void setHealthId(String healthId) {
        this.healthId = healthId;
    }
}