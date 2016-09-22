package org.sharedhealth.mci.model;


import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.util.Set;

import static org.sharedhealth.mci.util.Constants.*;

@Table(name = CF_PATIENT_DUPLICATE_IGNORED)
public class DuplicatePatientIgnored {

    @Column(name = HEALTH_ID1)
    @PartitionKey
    private String health_id1;

    @Column(name = HEALTH_ID2)
    @PartitionKey(value = 1)
    private String health_id2;

    @Column(name = REASONS)
    private Set<String> reasons;

    public DuplicatePatientIgnored() {
    }

    public DuplicatePatientIgnored(String healthId1, String healthId2, Set<String> reasons) {
        this.health_id1 = healthId1;
        this.health_id2 = healthId2;
        this.reasons = reasons;
    }

    public String getHealth_id1() {
        return health_id1;
    }

    public void setHealth_id1(String health_id1) {
        this.health_id1 = health_id1;
    }

    public String getHealth_id2() {
        return health_id2;
    }

    public void setHealth_id2(String health_id2) {
        this.health_id2 = health_id2;
    }

    public Set<String> getReasons() {
        return reasons;
    }

    public void setReasons(Set<String> reasons) {
        this.reasons = reasons;
    }
}
