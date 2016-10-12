package org.sharedhealth.mci.model;

public class IdentityStore {
    private String identityToken;

    public void setIdentityToken(String identityToken) {
        this.identityToken = identityToken;
    }

    public String getIdentityToken() {
        return identityToken;
    }

    public void clearIdentityToken() {
        identityToken = null;
    }

    public boolean hasIdentityToken() {
        return identityToken != null;
    }
}
