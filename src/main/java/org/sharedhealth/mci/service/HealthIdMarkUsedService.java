package org.sharedhealth.mci.service;

import org.sharedhealth.mci.WebClient;
import org.sharedhealth.mci.config.MCIProperties;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.sharedhealth.mci.util.HttpUtil.*;
import static org.sharedhealth.mci.util.StringUtils.ensureSuffix;
import static org.sharedhealth.mci.util.StringUtils.removePrefix;

public class HealthIdMarkUsedService {
    private MCIProperties mciProperties;
    private IdentityProviderService identityProviderService;
    private WebClient webClient;

    private final String USED_AT_KEY = "used_at";

    public HealthIdMarkUsedService(IdentityProviderService identityProviderService, WebClient webClient, MCIProperties mciProperties) {
        this.mciProperties = mciProperties;
        this.identityProviderService = identityProviderService;
        this.webClient = webClient;
    }

    public void markUsed(String healthId, UUID usedAt) throws IOException {
        Map<String, String> hidServiceHeaders = getHIDServiceHeaders();
        Map<String, String> data = new HashMap<>();
        data.put(USED_AT_KEY, usedAt.toString());
        webClient.put(getMarkUsedUrl(healthId), hidServiceHeaders, data);
    }

    private Map<String, String> getHIDServiceHeaders() throws IOException {
        Map<String, String> healthIdServiceHeader = new HashMap<>();
        String idpToken = identityProviderService.getOrCreateIdentityToken(mciProperties);
        healthIdServiceHeader.put(X_AUTH_TOKEN_KEY, idpToken);
        healthIdServiceHeader.put(CLIENT_ID_KEY, mciProperties.getIdpClientId());
        healthIdServiceHeader.put(FROM_KEY, mciProperties.getIdpEmail());
        return healthIdServiceHeader;
    }

    private String getMarkUsedUrl(String healthId) {
        String hidBaseUrl = ensureSuffix(mciProperties.getHidServiceBaseUrl(), URL_SEPARATOR);
        String markUsedPath = String.format(mciProperties.getHidServiceMarkUsedUrlPattern(), healthId);
        return hidBaseUrl + removePrefix(markUsedPath, URL_SEPARATOR);
    }


}
