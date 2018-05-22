package com.meh.stuff.facebook.parameter;

import java.util.Properties;

public class AppParameter {
    private String clientId;
    private String clientSecret;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void loadFromProperties(Properties properties) {
        setClientId(properties.getProperty("app.clientId"));
        setClientSecret(properties.getProperty("app.clientSecret"));
    }
}
