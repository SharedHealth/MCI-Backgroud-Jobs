package org.sharedhealth.mci.model;


import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.util.Date;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.sharedhealth.mci.util.Constants.*;

@Table(name = CF_PATIENT)
public class Patient {
    @PartitionKey
    @Column(name = HEALTH_ID)
    private String healthId;

    @Column(name = ASSIGNED_BY)
    private String assignedBy;

    @Column(name = NATIONAL_ID)
    private String nationalId;

    @Column(name = BIN_BRN)
    private String birthRegistrationNumber;

    @Column(name = FULL_NAME_BANGLA)
    private String fullNameBangla;

    @Column(name = GIVEN_NAME)
    private String givenName;

    @Column(name = SUR_NAME)
    private String surName;

    @Column(name = DATE_OF_BIRTH)
    private Date dateOfBirth;

    @Column(name = DOB_TYPE)
    private String dobType;

    @Column(name = GENDER)
    private String gender;

    @Column(name = OCCUPATION)
    private String occupation;

    @Column(name = EDU_LEVEL)
    private String educationLevel;

    @Column(name = FATHERS_NAME_BANGLA)
    private String fathersNameBangla;

    @Column(name = FATHERS_GIVEN_NAME)
    private String fathersGivenName;

    @Column(name = FATHERS_SUR_NAME)
    private String fathersSurName;

    @Column(name = FATHERS_UID)
    private String fathersUid;

    @Column(name = FATHERS_NID)
    private String fathersNid;

    @Column(name = FATHERS_BRN)
    private String fathersBrn;

    @Column(name = MOTHERS_NAME_BANGLA)
    private String mothersNameBangla;

    @Column(name = MOTHERS_GIVEN_NAME)
    private String mothersGivenName;

    @Column(name = MOTHERS_SUR_NAME)
    private String mothersSurName;

    @Column(name = MOTHERS_UID)
    private String mothersUid;

    @Column(name = MOTHERS_NID)
    private String mothersNid;

    @Column(name = MOTHERS_BRN)
    private String mothersBrn;

    @Column(name = UID)
    private String uid;

    @Column(name = PLACE_OF_BIRTH)
    private String placeOfBirth;

    @Column(name = MARITAL_STATUS)
    private String maritalStatus;

    @Column(name = RELIGION)
    private String religion;

    @Column(name = BLOOD_GROUP)
    private String bloodGroup;

    @Column(name = NATIONALITY)
    private String nationality;

    @Column(name = DISABILITY)
    private String disability;

    @Column(name = ETHNICITY)
    private String ethnicity;

    @Column(name = ADDRESS_LINE)
    private String addressLine;

    @Column(name = DIVISION_ID)
    private String divisionId;

    @Column(name = DISTRICT_ID)
    private String districtId;

    @Column(name = UPAZILA_ID)
    private String upazilaId;

    @Column(name = UNION_OR_URBAN_WARD_ID)
    private String unionOrUrbanWardId;

    @Column(name = HOLDING_NUMBER)
    private String holdingNumber;

    @Column(name = STREET)
    private String street;

    @Column(name = AREA_MOUJA)
    private String areaMouja;

    @Column(name = VILLAGE)
    private String village;

    @Column(name = POST_OFFICE)
    private String postOffice;

    @Column(name = POST_CODE)
    private String postCode;

    @Column(name = RURAL_WARD_ID)
    private String ruralWardId;

    @Column(name = CITY_CORPORATION)
    private String cityCorporationId;

    @Column(name = COUNTRY)
    private String countryCode;

    @Column(name = PERMANENT_ADDRESS_LINE)
    private String permanentAddressLine;

    @Column(name = PERMANENT_DIVISION_ID)
    private String permanentDivisionId;

    @Column(name = PERMANENT_DISTRICT_ID)
    private String permanentDistrictId;

    @Column(name = PERMANENT_UPAZILA_ID)
    private String permanentUpazilaId;

    @Column(name = PERMANENT_UNION_OR_URBAN_WARD_ID)
    private String permanentUnionOrUrbanWardId;

    @Column(name = PERMANENT_HOLDING_NUMBER)
    private String permanentHoldingNumber;

    @Column(name = PERMANENT_STREET)
    private String permanentStreet;

    @Column(name = PERMANENT_AREA_MOUJA)
    private String permanentAreaMouja;

    @Column(name = PERMANENT_VILLAGE)
    private String permanentVillage;

    @Column(name = PERMANENT_POST_OFFICE)
    private String permanentPostOffice;

    @Column(name = PERMANENT_POST_CODE)
    private String permanentPostCode;

    @Column(name = PERMANENT_RURAL_WARD_ID)
    private String permanentRuralWardId;

    @Column(name = PERMANENT_CITY_CORPORATION)
    private String permanentCityCorporationId;

    @Column(name = PERMANENT_COUNTRY)
    private String permanentCountryCode;

    @Column(name = STATUS)
    private String status;

    @Column(name = DATE_OF_DEATH)
    private Date dateOfDeath;

    @Column(name = PRIMARY_CONTACT)
    private String primaryContact;

    @Column(name = PRIMARY_CONTACT_NO)
    private String primaryCellNo;

    @Column(name = PHONE_NO)
    private String cellNo;

    @Column(name = PHONE_NUMBER_COUNTRY_CODE)
    private String phoneNumberCountryCode;

    @Column(name = PHONE_NUMBER_AREA_CODE)
    private String phoneNumberAreaCode;

    @Column(name = PHONE_NUMBER_EXTENSION)
    private String phoneNumberExtension;

    @Column(name = PRIMARY_CONTACT_NUMBER_COUNTRY_CODE)
    private String primaryContactNumberCountryCode;

    @Column(name = PRIMARY_CONTACT_NUMBER_AREA_CODE)
    private String primaryContactNumberAreaCode;

    @Column(name = PRIMARY_CONTACT_NUMBER_EXTENSION)
    private String primaryContactNumberExtension;

    @Column(name = CREATED_AT)
    private UUID createdAt;

    @Column(name = UPDATED_AT)
    private UUID updatedAt;

    @Column(name = CREATED_BY)
    private String createdBy;

    @Column(name = UPDATED_BY)
    private String updatedBy;

    @Column(name = RELATIONS)
    private String relations;

    @Column(name = PENDING_APPROVALS)
    private String pendingApprovals;

    @Column(name = HOUSEHOLD_CODE)
    private String householdCode;

    @Column(name = CONFIDENTIAL)
    private Boolean confidential;

    @Column(name = ACTIVE)
    private Boolean active;

    @Column(name = MERGED_WITH)
    private String mergedWith;

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getAreaMouja() {
        return areaMouja;
    }

    public void setAreaMouja(String areaMouja) {
        this.areaMouja = areaMouja;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public String getBirthRegistrationNumber() {
        return birthRegistrationNumber;
    }

    public void setBirthRegistrationNumber(String birthRegistrationNumber) {
        this.birthRegistrationNumber = birthRegistrationNumber;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getCellNo() {
        return cellNo;
    }

    public void setCellNo(String cellNo) {
        this.cellNo = cellNo;
    }

    public String getCityCorporationId() {
        return cityCorporationId;
    }

    public void setCityCorporationId(String cityCorporationId) {
        this.cityCorporationId = cityCorporationId;
    }

    public Boolean getConfidential() {
        return confidential;
    }

    public void setConfidential(Boolean confidential) {
        this.confidential = confidential;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public UUID getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(UUID createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Date getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(Date dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }

    public String getDisability() {
        return disability;
    }

    public void setDisability(String disability) {
        this.disability = disability;
    }

    public String getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(String divisionId) {
        this.divisionId = divisionId;
    }

    public String getDobType() {
        return dobType;
    }

    public void setDobType(String dobType) {
        this.dobType = dobType;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getFathersBrn() {
        return fathersBrn;
    }

    public void setFathersBrn(String fathersBrn) {
        this.fathersBrn = fathersBrn;
    }

    public String getFathersGivenName() {
        return fathersGivenName;
    }

    public void setFathersGivenName(String fathersGivenName) {
        this.fathersGivenName = fathersGivenName;
    }

    public String getFathersNameBangla() {
        return fathersNameBangla;
    }

    public void setFathersNameBangla(String fathersNameBangla) {
        this.fathersNameBangla = fathersNameBangla;
    }

    public String getFathersNid() {
        return fathersNid;
    }

    public void setFathersNid(String fathersNid) {
        this.fathersNid = fathersNid;
    }

    public String getFathersSurName() {
        return fathersSurName;
    }

    public void setFathersSurName(String fathersSurName) {
        this.fathersSurName = fathersSurName;
    }

    public String getFathersUid() {
        return fathersUid;
    }

    public void setFathersUid(String fathersUid) {
        this.fathersUid = fathersUid;
    }


    public String getFullNameBangla() {
        return fullNameBangla;
    }

    public void setFullNameBangla(String fullNameBangla) {
        this.fullNameBangla = fullNameBangla;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getHealthId() {
        return healthId;
    }

    public void setHealthId(String healthId) {
        this.healthId = healthId;
    }

    public String getHoldingNumber() {
        return holdingNumber;
    }

    public void setHoldingNumber(String holdingNumber) {
        this.holdingNumber = holdingNumber;
    }

    public String getHouseholdCode() {
        return householdCode;
    }

    public void setHouseholdCode(String householdCode) {
        this.householdCode = householdCode;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getMergedWith() {
        return mergedWith;
    }

    public void setMergedWith(String mergedWith) {
        this.mergedWith = mergedWith;
    }

    public String getMothersBrn() {
        return mothersBrn;
    }

    public void setMothersBrn(String mothersBrn) {
        this.mothersBrn = mothersBrn;
    }

    public String getMothersGivenName() {
        return mothersGivenName;
    }

    public void setMothersGivenName(String mothersGivenName) {
        this.mothersGivenName = mothersGivenName;
    }

    public String getMothersNameBangla() {
        return mothersNameBangla;
    }

    public void setMothersNameBangla(String mothersNameBangla) {
        this.mothersNameBangla = mothersNameBangla;
    }

    public String getMothersNid() {
        return mothersNid;
    }

    public void setMothersNid(String mothersNid) {
        this.mothersNid = mothersNid;
    }

    public String getMothersSurName() {
        return mothersSurName;
    }

    public void setMothersSurName(String mothersSurName) {
        this.mothersSurName = mothersSurName;
    }

    public String getMothersUid() {
        return mothersUid;
    }

    public void setMothersUid(String mothersUid) {
        this.mothersUid = mothersUid;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getPendingApprovals() {
        return pendingApprovals;
    }

    public void setPendingApprovals(String pendingApprovals) {
        this.pendingApprovals = pendingApprovals;
    }

    public String getPermanentAddressLine() {
        return permanentAddressLine;
    }

    public void setPermanentAddressLine(String permanentAddressLine) {
        this.permanentAddressLine = permanentAddressLine;
    }

    public String getPermanentAreaMouja() {
        return permanentAreaMouja;
    }

    public void setPermanentAreaMouja(String permanentAreaMouja) {
        this.permanentAreaMouja = permanentAreaMouja;
    }

    public String getPermanentCityCorporationId() {
        return permanentCityCorporationId;
    }

    public void setPermanentCityCorporationId(String permanentCityCorporationId) {
        this.permanentCityCorporationId = permanentCityCorporationId;
    }

    public String getPermanentCountryCode() {
        return permanentCountryCode;
    }

    public void setPermanentCountryCode(String permanentCountryCode) {
        this.permanentCountryCode = permanentCountryCode;
    }

    public String getPermanentDistrictId() {
        return permanentDistrictId;
    }

    public void setPermanentDistrictId(String permanentDistrictId) {
        this.permanentDistrictId = permanentDistrictId;
    }

    public String getPermanentDivisionId() {
        return permanentDivisionId;
    }

    public void setPermanentDivisionId(String permanentDivisionId) {
        this.permanentDivisionId = permanentDivisionId;
    }

    public String getPermanentHoldingNumber() {
        return permanentHoldingNumber;
    }

    public void setPermanentHoldingNumber(String permanentHoldingNumber) {
        this.permanentHoldingNumber = permanentHoldingNumber;
    }

    public String getPermanentPostCode() {
        return permanentPostCode;
    }

    public void setPermanentPostCode(String permanentPostCode) {
        this.permanentPostCode = permanentPostCode;
    }

    public String getPermanentPostOffice() {
        return permanentPostOffice;
    }

    public void setPermanentPostOffice(String permanentPostOffice) {
        this.permanentPostOffice = permanentPostOffice;
    }

    public String getPermanentRuralWardId() {
        return permanentRuralWardId;
    }

    public void setPermanentRuralWardId(String permanentRuralWardId) {
        this.permanentRuralWardId = permanentRuralWardId;
    }

    public String getPermanentStreet() {
        return permanentStreet;
    }

    public void setPermanentStreet(String permanentStreet) {
        this.permanentStreet = permanentStreet;
    }

    public String getPermanentUnionOrUrbanWardId() {
        return permanentUnionOrUrbanWardId;
    }

    public void setPermanentUnionOrUrbanWardId(String permanentUnionOrUrbanWardId) {
        this.permanentUnionOrUrbanWardId = permanentUnionOrUrbanWardId;
    }

    public String getPermanentUpazilaId() {
        return permanentUpazilaId;
    }

    public void setPermanentUpazilaId(String permanentUpazilaId) {
        this.permanentUpazilaId = permanentUpazilaId;
    }

    public String getPermanentVillage() {
        return permanentVillage;
    }

    public void setPermanentVillage(String permanentVillage) {
        this.permanentVillage = permanentVillage;
    }

    public String getPhoneNumberAreaCode() {
        return phoneNumberAreaCode;
    }

    public void setPhoneNumberAreaCode(String phoneNumberAreaCode) {
        this.phoneNumberAreaCode = phoneNumberAreaCode;
    }

    public String getPhoneNumberCountryCode() {
        return phoneNumberCountryCode;
    }

    public void setPhoneNumberCountryCode(String phoneNumberCountryCode) {
        this.phoneNumberCountryCode = phoneNumberCountryCode;
    }

    public String getPhoneNumberExtension() {
        return phoneNumberExtension;
    }

    public void setPhoneNumberExtension(String phoneNumberExtension) {
        this.phoneNumberExtension = phoneNumberExtension;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getPostOffice() {
        return postOffice;
    }

    public void setPostOffice(String postOffice) {
        this.postOffice = postOffice;
    }

    public String getPrimaryCellNo() {
        return primaryCellNo;
    }

    public void setPrimaryCellNo(String primaryCellNo) {
        this.primaryCellNo = primaryCellNo;
    }

    public String getPrimaryContact() {
        return primaryContact;
    }

    public void setPrimaryContact(String primaryContact) {
        this.primaryContact = primaryContact;
    }

    public String getPrimaryContactNumberAreaCode() {
        return primaryContactNumberAreaCode;
    }

    public void setPrimaryContactNumberAreaCode(String primaryContactNumberAreaCode) {
        this.primaryContactNumberAreaCode = primaryContactNumberAreaCode;
    }

    public String getPrimaryContactNumberCountryCode() {
        return primaryContactNumberCountryCode;
    }

    public void setPrimaryContactNumberCountryCode(String primaryContactNumberCountryCode) {
        this.primaryContactNumberCountryCode = primaryContactNumberCountryCode;
    }

    public String getPrimaryContactNumberExtension() {
        return primaryContactNumberExtension;
    }

    public void setPrimaryContactNumberExtension(String primaryContactNumberExtension) {
        this.primaryContactNumberExtension = primaryContactNumberExtension;
    }

    public String getRelations() {
        return relations;
    }

    public void setRelations(String relations) {
        this.relations = relations;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getRuralWardId() {
        return ruralWardId;
    }

    public void setRuralWardId(String ruralWardId) {
        this.ruralWardId = ruralWardId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUnionOrUrbanWardId() {
        return unionOrUrbanWardId;
    }

    public void setUnionOrUrbanWardId(String unionOrUrbanWardId) {
        this.unionOrUrbanWardId = unionOrUrbanWardId;
    }

    public String getUpazilaId() {
        return upazilaId;
    }

    public void setUpazilaId(String upazilaId) {
        this.upazilaId = upazilaId;
    }

    public UUID getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(UUID updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient)) return false;

        Patient patient = (Patient) o;

        if (addressLine != null ? !addressLine.equals(patient.addressLine) : patient.addressLine != null) return false;
        if (areaMouja != null ? !areaMouja.equals(patient.areaMouja) : patient.areaMouja != null) return false;
        if (birthRegistrationNumber != null ? !birthRegistrationNumber.equals(patient.birthRegistrationNumber) : patient.birthRegistrationNumber != null)
            return false;
        if (bloodGroup != null ? !bloodGroup.equals(patient.bloodGroup) : patient.bloodGroup != null) return false;
        if (cellNo != null ? !cellNo.equals(patient.cellNo) : patient.cellNo != null) return false;
        if (cityCorporationId != null ? !cityCorporationId.equals(patient.cityCorporationId) : patient.cityCorporationId != null)
            return false;
        if (confidential != null ? !confidential.equals(patient.confidential) : patient.confidential != null)
            return false;
        if (countryCode != null ? !countryCode.equals(patient.countryCode) : patient.countryCode != null) return false;
        if (createdAt != null ? !createdAt.equals(patient.createdAt) : patient.createdAt != null) return false;
        if (createdBy != null ? !createdBy.equals(patient.createdBy) : patient.createdBy != null) return false;
        if (dateOfBirth != null ? !dateOfBirth.equals(patient.dateOfBirth) : patient.dateOfBirth != null) return false;
        if (dobType != null ? !dobType.equals(patient.dobType) : patient.dobType != null) return false;
        if (dateOfDeath != null ? !dateOfDeath.equals(patient.dateOfDeath) : patient.dateOfDeath != null) return false;
        if (disability != null ? !disability.equals(patient.disability) : patient.disability != null) return false;
        if (districtId != null ? !districtId.equals(patient.districtId) : patient.districtId != null) return false;
        if (divisionId != null ? !divisionId.equals(patient.divisionId) : patient.divisionId != null) return false;
        if (educationLevel != null ? !educationLevel.equals(patient.educationLevel) : patient.educationLevel != null)
            return false;
        if (ethnicity != null ? !ethnicity.equals(patient.ethnicity) : patient.ethnicity != null) return false;
        if (fathersBrn != null ? !fathersBrn.equals(patient.fathersBrn) : patient.fathersBrn != null) return false;
        if (fathersGivenName != null ? !fathersGivenName.equals(patient.fathersGivenName) : patient.fathersGivenName != null)
            return false;
        if (fathersNameBangla != null ? !fathersNameBangla.equals(patient.fathersNameBangla) : patient.fathersNameBangla != null)
            return false;
        if (fathersNid != null ? !fathersNid.equals(patient.fathersNid) : patient.fathersNid != null) return false;
        if (fathersSurName != null ? !fathersSurName.equals(patient.fathersSurName) : patient.fathersSurName != null)
            return false;
        if (fathersUid != null ? !fathersUid.equals(patient.fathersUid) : patient.fathersUid != null) return false;
        if (fullNameBangla != null ? !fullNameBangla.equals(patient.fullNameBangla) : patient.fullNameBangla != null)
            return false;
        if (gender != null ? !gender.equals(patient.gender) : patient.gender != null) return false;
        if (givenName != null ? !givenName.equals(patient.givenName) : patient.givenName != null) return false;
        if (healthId != null ? !healthId.equals(patient.healthId) : patient.healthId != null) return false;
        if (holdingNumber != null ? !holdingNumber.equals(patient.holdingNumber) : patient.holdingNumber != null)
            return false;
        if (maritalStatus != null ? !maritalStatus.equals(patient.maritalStatus) : patient.maritalStatus != null)
            return false;
        if (mothersBrn != null ? !mothersBrn.equals(patient.mothersBrn) : patient.mothersBrn != null) return false;
        if (mothersGivenName != null ? !mothersGivenName.equals(patient.mothersGivenName) : patient.mothersGivenName != null)
            return false;
        if (mothersNameBangla != null ? !mothersNameBangla.equals(patient.mothersNameBangla) : patient.mothersNameBangla != null)
            return false;
        if (mothersNid != null ? !mothersNid.equals(patient.mothersNid) : patient.mothersNid != null) return false;
        if (mothersSurName != null ? !mothersSurName.equals(patient.mothersSurName) : patient.mothersSurName != null)
            return false;
        if (mothersUid != null ? !mothersUid.equals(patient.mothersUid) : patient.mothersUid != null) return false;
        if (nationalId != null ? !nationalId.equals(patient.nationalId) : patient.nationalId != null) return false;
        if (nationality != null ? !nationality.equals(patient.nationality) : patient.nationality != null) return false;
        if (occupation != null ? !occupation.equals(patient.occupation) : patient.occupation != null) return false;
        if (pendingApprovals != null ? !pendingApprovals.equals(patient.pendingApprovals) : patient.pendingApprovals != null)
            return false;
        if (permanentAddressLine != null ? !permanentAddressLine.equals(patient.permanentAddressLine) : patient.permanentAddressLine != null)
            return false;
        if (permanentAreaMouja != null ? !permanentAreaMouja.equals(patient.permanentAreaMouja) : patient.permanentAreaMouja != null)
            return false;
        if (permanentCityCorporationId != null ? !permanentCityCorporationId.equals(patient.permanentCityCorporationId) : patient.permanentCityCorporationId != null)
            return false;
        if (permanentCountryCode != null ? !permanentCountryCode.equals(patient.permanentCountryCode) : patient.permanentCountryCode != null)
            return false;
        if (permanentDistrictId != null ? !permanentDistrictId.equals(patient.permanentDistrictId) : patient.permanentDistrictId != null)
            return false;
        if (permanentDivisionId != null ? !permanentDivisionId.equals(patient.permanentDivisionId) : patient.permanentDivisionId != null)
            return false;
        if (permanentHoldingNumber != null ? !permanentHoldingNumber.equals(patient.permanentHoldingNumber) : patient.permanentHoldingNumber != null)
            return false;
        if (permanentPostCode != null ? !permanentPostCode.equals(patient.permanentPostCode) : patient.permanentPostCode != null)
            return false;
        if (permanentPostOffice != null ? !permanentPostOffice.equals(patient.permanentPostOffice) : patient.permanentPostOffice != null)
            return false;
        if (permanentRuralWardId != null ? !permanentRuralWardId.equals(patient.permanentRuralWardId) : patient.permanentRuralWardId != null)
            return false;
        if (permanentStreet != null ? !permanentStreet.equals(patient.permanentStreet) : patient.permanentStreet != null)
            return false;
        if (permanentUnionOrUrbanWardId != null ? !permanentUnionOrUrbanWardId.equals(patient.permanentUnionOrUrbanWardId) : patient.permanentUnionOrUrbanWardId != null)
            return false;
        if (permanentUpazilaId != null ? !permanentUpazilaId.equals(patient.permanentUpazilaId) : patient.permanentUpazilaId != null)
            return false;
        if (permanentVillage != null ? !permanentVillage.equals(patient.permanentVillage) : patient.permanentVillage != null)
            return false;
        if (phoneNumberAreaCode != null ? !phoneNumberAreaCode.equals(patient.phoneNumberAreaCode) : patient.phoneNumberAreaCode != null)
            return false;
        if (phoneNumberCountryCode != null ? !phoneNumberCountryCode.equals(patient.phoneNumberCountryCode) : patient.phoneNumberCountryCode != null)
            return false;
        if (phoneNumberExtension != null ? !phoneNumberExtension.equals(patient.phoneNumberExtension) : patient.phoneNumberExtension != null)
            return false;
        if (placeOfBirth != null ? !placeOfBirth.equals(patient.placeOfBirth) : patient.placeOfBirth != null)
            return false;
        if (postCode != null ? !postCode.equals(patient.postCode) : patient.postCode != null) return false;
        if (postOffice != null ? !postOffice.equals(patient.postOffice) : patient.postOffice != null) return false;
        if (primaryCellNo != null ? !primaryCellNo.equals(patient.primaryCellNo) : patient.primaryCellNo != null)
            return false;
        if (primaryContact != null ? !primaryContact.equals(patient.primaryContact) : patient.primaryContact != null)
            return false;
        if (primaryContactNumberAreaCode != null ? !primaryContactNumberAreaCode.equals(patient.primaryContactNumberAreaCode) : patient.primaryContactNumberAreaCode != null)
            return false;
        if (primaryContactNumberCountryCode != null ? !primaryContactNumberCountryCode.equals(patient.primaryContactNumberCountryCode) : patient.primaryContactNumberCountryCode != null)
            return false;
        if (primaryContactNumberExtension != null ? !primaryContactNumberExtension.equals(patient.primaryContactNumberExtension) : patient.primaryContactNumberExtension != null)
            return false;
        if (relations != null ? !relations.equals(patient.relations) : patient.relations != null) return false;
        if (religion != null ? !religion.equals(patient.religion) : patient.religion != null) return false;
        if (ruralWardId != null ? !ruralWardId.equals(patient.ruralWardId) : patient.ruralWardId != null) return false;
        if (status != null ? !status.equals(patient.status) : patient.status != null) return false;
        if (street != null ? !street.equals(patient.street) : patient.street != null) return false;
        if (surName != null ? !surName.equals(patient.surName) : patient.surName != null) return false;
        if (uid != null ? !uid.equals(patient.uid) : patient.uid != null) return false;
        if (unionOrUrbanWardId != null ? !unionOrUrbanWardId.equals(patient.unionOrUrbanWardId) : patient.unionOrUrbanWardId != null)
            return false;
        if (upazilaId != null ? !upazilaId.equals(patient.upazilaId) : patient.upazilaId != null) return false;
        if (updatedAt != null ? !updatedAt.equals(patient.updatedAt) : patient.updatedAt != null) return false;
        if (updatedBy != null ? !updatedBy.equals(patient.updatedBy) : patient.updatedBy != null) return false;
        if (village != null ? !village.equals(patient.village) : patient.village != null) return false;
        if (householdCode != null ? !householdCode.equals(patient.householdCode) : patient.householdCode != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = healthId != null ? healthId.hashCode() : 0;
        result = 31 * result + (nationalId != null ? nationalId.hashCode() : 0);
        result = 31 * result + (birthRegistrationNumber != null ? birthRegistrationNumber.hashCode() : 0);
        result = 31 * result + (fullNameBangla != null ? fullNameBangla.hashCode() : 0);
        result = 31 * result + (givenName != null ? givenName.hashCode() : 0);
        result = 31 * result + (surName != null ? surName.hashCode() : 0);
        result = 31 * result + (dateOfBirth != null ? dateOfBirth.hashCode() : 0);
        result = 31 * result + (dobType != null ? dobType.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (occupation != null ? occupation.hashCode() : 0);
        result = 31 * result + (educationLevel != null ? educationLevel.hashCode() : 0);
        result = 31 * result + (fathersNameBangla != null ? fathersNameBangla.hashCode() : 0);
        result = 31 * result + (fathersGivenName != null ? fathersGivenName.hashCode() : 0);
        result = 31 * result + (fathersSurName != null ? fathersSurName.hashCode() : 0);
        result = 31 * result + (fathersUid != null ? fathersUid.hashCode() : 0);
        result = 31 * result + (fathersNid != null ? fathersNid.hashCode() : 0);
        result = 31 * result + (fathersBrn != null ? fathersBrn.hashCode() : 0);
        result = 31 * result + (mothersNameBangla != null ? mothersNameBangla.hashCode() : 0);
        result = 31 * result + (mothersGivenName != null ? mothersGivenName.hashCode() : 0);
        result = 31 * result + (mothersSurName != null ? mothersSurName.hashCode() : 0);
        result = 31 * result + (mothersUid != null ? mothersUid.hashCode() : 0);
        result = 31 * result + (mothersNid != null ? mothersNid.hashCode() : 0);
        result = 31 * result + (mothersBrn != null ? mothersBrn.hashCode() : 0);
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        result = 31 * result + (placeOfBirth != null ? placeOfBirth.hashCode() : 0);
        result = 31 * result + (maritalStatus != null ? maritalStatus.hashCode() : 0);
        result = 31 * result + (religion != null ? religion.hashCode() : 0);
        result = 31 * result + (bloodGroup != null ? bloodGroup.hashCode() : 0);
        result = 31 * result + (nationality != null ? nationality.hashCode() : 0);
        result = 31 * result + (disability != null ? disability.hashCode() : 0);
        result = 31 * result + (ethnicity != null ? ethnicity.hashCode() : 0);
        result = 31 * result + (addressLine != null ? addressLine.hashCode() : 0);
        result = 31 * result + (divisionId != null ? divisionId.hashCode() : 0);
        result = 31 * result + (districtId != null ? districtId.hashCode() : 0);
        result = 31 * result + (upazilaId != null ? upazilaId.hashCode() : 0);
        result = 31 * result + (unionOrUrbanWardId != null ? unionOrUrbanWardId.hashCode() : 0);
        result = 31 * result + (holdingNumber != null ? holdingNumber.hashCode() : 0);
        result = 31 * result + (street != null ? street.hashCode() : 0);
        result = 31 * result + (areaMouja != null ? areaMouja.hashCode() : 0);
        result = 31 * result + (village != null ? village.hashCode() : 0);
        result = 31 * result + (postOffice != null ? postOffice.hashCode() : 0);
        result = 31 * result + (postCode != null ? postCode.hashCode() : 0);
        result = 31 * result + (ruralWardId != null ? ruralWardId.hashCode() : 0);
        result = 31 * result + (cityCorporationId != null ? cityCorporationId.hashCode() : 0);
        result = 31 * result + (countryCode != null ? countryCode.hashCode() : 0);
        result = 31 * result + (permanentAddressLine != null ? permanentAddressLine.hashCode() : 0);
        result = 31 * result + (permanentDivisionId != null ? permanentDivisionId.hashCode() : 0);
        result = 31 * result + (permanentDistrictId != null ? permanentDistrictId.hashCode() : 0);
        result = 31 * result + (permanentUpazilaId != null ? permanentUpazilaId.hashCode() : 0);
        result = 31 * result + (permanentUnionOrUrbanWardId != null ? permanentUnionOrUrbanWardId.hashCode() : 0);
        result = 31 * result + (permanentHoldingNumber != null ? permanentHoldingNumber.hashCode() : 0);
        result = 31 * result + (permanentStreet != null ? permanentStreet.hashCode() : 0);
        result = 31 * result + (permanentAreaMouja != null ? permanentAreaMouja.hashCode() : 0);
        result = 31 * result + (permanentVillage != null ? permanentVillage.hashCode() : 0);
        result = 31 * result + (permanentPostOffice != null ? permanentPostOffice.hashCode() : 0);
        result = 31 * result + (permanentPostCode != null ? permanentPostCode.hashCode() : 0);
        result = 31 * result + (permanentRuralWardId != null ? permanentRuralWardId.hashCode() : 0);
        result = 31 * result + (permanentCityCorporationId != null ? permanentCityCorporationId.hashCode() : 0);
        result = 31 * result + (permanentCountryCode != null ? permanentCountryCode.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (dateOfDeath != null ? dateOfDeath.hashCode() : 0);
        result = 31 * result + (primaryContact != null ? primaryContact.hashCode() : 0);
        result = 31 * result + (primaryCellNo != null ? primaryCellNo.hashCode() : 0);
        result = 31 * result + (cellNo != null ? cellNo.hashCode() : 0);
        result = 31 * result + (phoneNumberCountryCode != null ? phoneNumberCountryCode.hashCode() : 0);
        result = 31 * result + (phoneNumberAreaCode != null ? phoneNumberAreaCode.hashCode() : 0);
        result = 31 * result + (phoneNumberExtension != null ? phoneNumberExtension.hashCode() : 0);
        result = 31 * result + (primaryContactNumberCountryCode != null ? primaryContactNumberCountryCode.hashCode() : 0);
        result = 31 * result + (primaryContactNumberAreaCode != null ? primaryContactNumberAreaCode.hashCode() : 0);
        result = 31 * result + (primaryContactNumberExtension != null ? primaryContactNumberExtension.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (updatedBy != null ? updatedBy.hashCode() : 0);
        result = 31 * result + (relations != null ? relations.hashCode() : 0);
        result = 31 * result + (pendingApprovals != null ? pendingApprovals.hashCode() : 0);
        result = 31 * result + (confidential != null ? confidential.hashCode() : 0);
        result = 31 * result + (householdCode != null ? householdCode.hashCode() : 0);
        return result;
    }

    public Catchment getCatchment() {
        if (isBlank(divisionId) || isBlank(districtId)) {
            return null;
        }
        return new Catchment(divisionId, districtId, upazilaId,
                cityCorporationId, unionOrUrbanWardId, ruralWardId);
    }
}
