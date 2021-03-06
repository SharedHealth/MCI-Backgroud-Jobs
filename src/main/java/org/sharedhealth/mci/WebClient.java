package org.sharedhealth.mci;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.sharedhealth.mci.exception.IdentityUnauthorizedException;
import org.sharedhealth.mci.model.IdentityStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class WebClient {
    private IdentityStore identityStore;

    public WebClient(IdentityStore identityStore) {
        this.identityStore = identityStore;
    }

    private final static Logger logger = LogManager.getLogger(WebClient.class);

    public String post(String url, Map<String, String> headers, Map<String, String> formEntities) throws IOException {
        logger.debug("HTTP POST request for {}", url);
        HttpPost request = new HttpPost(url);
        addHeaders(headers, request);
        List<NameValuePair> valuePairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : formEntities.entrySet()) {
            valuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(valuePairs);
        request.setEntity(formEntity);
        return execute(request);
    }


    public String put(String url, Map<String, String> headers, Map<String, String> data) throws IOException {
        logger.debug("HTTP put request for {}", url);
        HttpPut request = new HttpPut(url);
        addHeaders(headers, request);
        String body = new ObjectMapper().writeValueAsString(data);
        StringEntity stringEntity = new StringEntity(body, ContentType.APPLICATION_JSON);
        request.setEntity(stringEntity);
        return execute(request);
    }

    private void addHeaders(Map<String, String> headers, HttpRequestBase request) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            request.addHeader(entry.getKey(), entry.getValue());
        }
    }

    private String execute(HttpRequestBase request) throws IOException {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            ResponseHandler<String> responseHandler = response -> {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode >= 200 && statusCode < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? parseContentInputString(entity) : null;
                } else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                    identityStore.clearIdentityToken();
                    throw new IdentityUnauthorizedException("Identity not authorized.");
                } else {
                    throw new ClientProtocolException("Unexpected Response status.");
                }
            };
            return client.execute(request, responseHandler);
        }
    }

    private String parseContentInputString(HttpEntity entity) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent()));
        String inputLine;
        StringBuilder responseString = new StringBuilder();
        while ((inputLine = bufferedReader.readLine()) != null) {
            responseString.append(inputLine);
        }
        bufferedReader.close();
        return responseString.toString().replace("\uFEFF", "");
    }
}