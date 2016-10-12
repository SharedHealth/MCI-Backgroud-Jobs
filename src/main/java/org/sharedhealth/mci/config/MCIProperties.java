package org.sharedhealth.mci.config;

import java.util.Map;

public class MCIProperties {
    private static MCIProperties mciProperties;
    private String cassandraKeySpace;
    private String cassandraHost;
    private String cassandraPort;

    private String cassandraUser;
    private String cassandraPassword;
    private String cassandraTimeout;
    private String cassandraVersion;

    private String idpBaseUrl;
    private String idpSignInPath;
    private String idpClientId;
    private String idpXAuthToken;
    private String idpEmail;
    private String idpPassword;

    private String hidServiceBaseUrl;
    private String hidServiceMarkUsedUrlPattern;

    private MCIProperties() {
        Map<String, String> env = System.getenv();
        this.cassandraKeySpace = env.get("CASSANDRA_KEYSPACE");
        this.cassandraHost = env.get("CASSANDRA_HOST");
        this.cassandraPort = env.get("CASSANDRA_PORT");
        this.cassandraPassword = env.get("CASSANDRA_PASSWORD");
        this.cassandraUser = env.get("CASSANDRA_USER");
        this.cassandraTimeout = env.get("CASSANDRA_TIMEOUT");
        this.cassandraVersion = env.get("CASSANDRA_VERSION");

        this.idpBaseUrl = env.get("IDP_BASE_URL");
        this.idpSignInPath = env.get("IDP_SIGNIN_PATH");
        this.idpClientId = env.get("IDP_CLIENT_ID");
        this.idpXAuthToken = env.get("IDP_X_AUTH_TOKEN");
        this.idpEmail = env.get("IDP_EMAIL");
        this.idpPassword = env.get("IDP_PASSWORD");

        this.hidServiceBaseUrl = env.get("HID_SERVICE_BASE_URL");
        this.hidServiceMarkUsedUrlPattern = env.get("HID_SERVICE_MARK_USED_URL");
    }

    public static MCIProperties getInstance() {
        if (mciProperties != null) return mciProperties;
        mciProperties = new MCIProperties();
        return mciProperties;
    }

    public String getCassandraKeySpace() {
        return cassandraKeySpace;
    }

    public String getCassandraHost() {
        return cassandraHost;
    }

    public int getCassandraPort() {
        return Integer.parseInt(cassandraPort);
    }

    public String getCassandraUser() {
        return cassandraUser;
    }

    public String getCassandraPassword() {
        return cassandraPassword;
    }

    public int getCassandraTimeout() {
        return Integer.parseInt(cassandraTimeout);
    }

    public int getCassandraVersion() {
        return Integer.parseInt(cassandraVersion);
    }

    public String getIdpXAuthToken() {
        return idpXAuthToken;
    }

    public String getIdpEmail() {
        return idpEmail;
    }

    public String getIdpPassword() {
        return idpPassword;
    }

    public String getIdpBaseUrl() {
        return idpBaseUrl;
    }

    public String getIdpSignInPath() {
        return idpSignInPath;
    }

    public String getIdpClientId() {
        return idpClientId;
    }

    public String getHidServiceBaseUrl() {
        return hidServiceBaseUrl;
    }

    public String getHidServiceMarkUsedUrlPattern() {
        return hidServiceMarkUsedUrlPattern;
    }
}
