package org.sharedhealth.mci.service;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.sharedhealth.mci.BaseIntegrationTest;
import org.sharedhealth.mci.WebClient;
import org.sharedhealth.mci.config.MCIProperties;
import org.sharedhealth.mci.model.IdentityStore;
import org.sharedhealth.mci.util.TestUtils;
import org.sharedhealth.mci.util.TimeUuidUtil;

import java.util.Date;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.sharedhealth.mci.util.HttpUtil.*;

public class HealthIdMarkUsedServiceIT extends BaseIntegrationTest {
    private final String SIGN_IN_URL = "/signin";
    private String HID_MARK_USED_URL = "/healthIds/markUsed/HID";

    private HealthIdMarkUsedService healthIdMarkUsedService;
    private MCIProperties mciProperties;
    private IdentityStore identityStore;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9997);

    @Before
    public void setUp() throws Exception {
        mciProperties = MCIProperties.getInstance();
        identityStore = new IdentityStore();
        WebClient webClient = new WebClient(identityStore);
        IdentityProviderService identityProviderService = new IdentityProviderService(webClient, identityStore);
        healthIdMarkUsedService = new HealthIdMarkUsedService(identityProviderService, webClient, mciProperties);
    }

    @After
    public void tearDown() throws Exception {
        TestUtils.truncateAllColumnFamilies();
        identityStore.clearIdentityToken();
    }

    @Test
    public void shouldInformHealthIdServiceOfUsedHealthId() throws Exception {
        UUID token = UUID.randomUUID();
        UUID usedAt = TimeUuidUtil.uuidForDate(new Date());

        setUpIdentityStub(token);
        setupMarkUsedStub(token, usedAt);

        healthIdMarkUsedService.markUsed("HID", usedAt);

        verify(1, postRequestedFor(urlMatching(SIGN_IN_URL)));
        verify(1, putRequestedFor(urlMatching(HID_MARK_USED_URL)));
        assertTrue(identityStore.hasIdentityToken());
    }

    @Test
    public void shouldClearIdentityTokenOnUnauthorized() throws Exception {
        UUID token = UUID.randomUUID();
        setUpIdentityStub(token);
        stubFor(put(urlPathEqualTo(HID_MARK_USED_URL))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_UNAUTHORIZED)
                ));

        healthIdMarkUsedService.markUsed("HID", TimeUuidUtil.uuidForDate(new Date()));

        verify(1, postRequestedFor(urlMatching(SIGN_IN_URL)));
        verify(1, putRequestedFor(urlMatching(HID_MARK_USED_URL)));
        assertFalse(identityStore.hasIdentityToken());
    }

    private void setUpIdentityStub(UUID uuid) {
        String idpResponse = "{\"access_token\" : \"" + uuid.toString() + "\"}";

        stubFor(post(urlMatching(SIGN_IN_URL))
                .withHeader(X_AUTH_TOKEN_KEY, equalTo(mciProperties.getIdpXAuthToken()))
                .withHeader(CLIENT_ID_KEY, equalTo(mciProperties.getIdpClientId()))
                .withRequestBody(containing("password=password&email=shrSysAdmin%40gmail.com"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withBody(idpResponse)
                ));
    }

    private void setupMarkUsedStub(UUID token, UUID usedAt) {
        stubFor(put(urlPathEqualTo(HID_MARK_USED_URL))
                .withHeader(X_AUTH_TOKEN_KEY, equalTo(token.toString()))
                .withHeader(CLIENT_ID_KEY, equalTo(mciProperties.getIdpClientId()))
                .withHeader(FROM_KEY, equalTo(mciProperties.getIdpEmail()))
                .withRequestBody(equalToJson("{used_at:" + usedAt.toString() + "}"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withBody("Accepted")
                ));
    }
}