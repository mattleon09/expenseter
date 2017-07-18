package com.mattleo.finance.backend.endpoint.body;

public class EntitiesBody<T> {
    private final String deviceRegId;

    public EntitiesBody(String deviceRegId) {
        this.deviceRegId = deviceRegId;
    }

    public String getDeviceRegId() {
        return deviceRegId;
    }
}
