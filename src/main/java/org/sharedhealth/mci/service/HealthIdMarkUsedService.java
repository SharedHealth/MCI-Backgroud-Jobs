package org.sharedhealth.mci.service;

import org.sharedhealth.mci.WebClient;
import org.sharedhealth.mci.config.MCIProperties;
import org.sharedhealth.mci.util.TimeUuidUtil;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.sharedhealth.mci.util.HttpUtil.*;
import static org.sharedhealth.mci.util.StringUtils.ensureSuffix;
import static org.sharedhealth.mci.util.StringUtils.removePrefix;

public class HealthIdMarkUsedService {
    private MCIProperties mciProperties;
    private IdentityProviderService identityProviderService;

    private final String USED_AT_KEY = "used_at";

    public HealthIdMarkUsedService(MCIProperties mciProperties, IdentityProviderService identityProviderService) {
        this.mciProperties = mciProperties;
        this.identityProviderService = identityProviderService;
    }

    public void notifyHealthIdService(String healthId) throws IOException {
        UUID usedAt = TimeUuidUtil.uuidForDate(new Date());

        Map<String, String> hidServiceHeaders = getHIDServiceHeaders();
        Map<String, String> data = new HashMap<>();
        data.put(USED_AT_KEY, usedAt.toString());
        new WebClient().put(getMarkUsedUrl(healthId), hidServiceHeaders, data);
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
