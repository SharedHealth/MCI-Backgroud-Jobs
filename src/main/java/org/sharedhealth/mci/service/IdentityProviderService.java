package org.sharedhealth.mci.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.sharedhealth.mci.WebClient;
import org.sharedhealth.mci.config.MCIProperties;
import org.sharedhealth.mci.model.IdentityStore;
import org.sharedhealth.mci.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.sharedhealth.mci.util.HttpUtil.*;

public class IdentityProviderService {
    private final static Logger logger = LogManager.getLogger(IdentityProviderService.class);

    private IdentityStore identityStore;
    private WebClient webClient;

    public IdentityProviderService(WebClient webClient, IdentityStore identityStore) {
        this.identityStore = identityStore;
        this.webClient = webClient;
    }

    public String getOrCreateIdentityToken(MCIProperties mciProperties) throws IOException {
        if (!identityStore.hasIdentityToken()) {
            identityStore.setIdentityToken(getIdentityTokenFromIdp(mciProperties));
        }
        return identityStore.getIdentityToken();
    }

    private String getIdentityTokenFromIdp(MCIProperties mciProperties) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put(X_AUTH_TOKEN_KEY, mciProperties.getIdpXAuthToken());
        headers.put(CLIENT_ID_KEY, mciProperties.getIdpClientId());
        Map<String, String> formEntities = new HashMap<>();
        formEntities.put(EMAIL_KEY, mciProperties.getIdpEmail());
        formEntities.put(PASSWORD_KEY, mciProperties.getIdpPassword());
        String response = webClient.post(getIdpSignInURL(mciProperties), headers, formEntities);
        if (response != null) {
            Map map = new ObjectMapper().readValue(response, Map.class);
            return (String) map.get(ACCESS_TOKEN_KEY);
        }
        return null;
    }

    private String getIdpSignInURL(MCIProperties mciProperties) {
        String idpBaseUrl = StringUtils.ensureSuffix(mciProperties.getIdpBaseUrl(), URL_SEPARATOR);
        String idpSignInPath = StringUtils.removePrefix(mciProperties.getIdpSignInPath(), URL_SEPARATOR);
        return idpBaseUrl + idpSignInPath;
    }
}
