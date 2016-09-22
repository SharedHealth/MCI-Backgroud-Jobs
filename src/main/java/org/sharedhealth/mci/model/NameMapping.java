package org.sharedhealth.mci.model;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import static org.sharedhealth.mci.util.Constants.*;

@Table(name = CF_NAME_MAPPING)
public class NameMapping {

    @Column(name = DIVISION_ID)
    @PartitionKey
    private String divisionId;

    @Column(name = DISTRICT_ID)
    @PartitionKey(value = 1)
    private String districtId;

    @Column(name = UPAZILA_ID)
    @PartitionKey(value = 2)
    private String upazilaId;

    @Column(name = GIVEN_NAME)
    @PartitionKey(value = 3)
    private String givenName;

    @Column(name = SUR_NAME)
    @PartitionKey(value = 4)
    private String surName;

    @Column(name = HEALTH_ID)
    @PartitionKey(value = 5)
    private String healthId;

    public NameMapping() {
    }

    public NameMapping(String divisionId, String districtId, String upazilaId, String givenName, String surname, String healthId) {
        this.divisionId = divisionId;
        this.districtId = districtId;
        this.upazilaId = upazilaId;
        this.givenName = givenName;
        this.surName = surname;
        this.healthId = healthId;
    }

    public String getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(String divisionId) {
        this.divisionId = divisionId;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getUpazilaId() {
        return upazilaId;
    }

    public void setUpazilaId(String upazilaId) {
        this.upazilaId = upazilaId;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getHealthId() {
        return healthId;
    }

    public void setHealthId(String healthId) {
        this.healthId = healthId;
    }
}