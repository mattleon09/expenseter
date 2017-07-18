package com.mattleo.finance.backend.endpoint.body;

import com.mattleo.finance.common.utils.Strings;
import com.google.api.server.spi.response.BadRequestException;
import com.google.gson.annotations.SerializedName;

public class RegisterDeviceBody implements Body {
    @SerializedName(value = "registration_id")
    private String regId;

    public void verifyRequiredFields() throws BadRequestException {
        if (Strings.isEmpty(regId)) {
            throw new BadRequestException("registration_id cannot be empty.");
        }
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }
}
