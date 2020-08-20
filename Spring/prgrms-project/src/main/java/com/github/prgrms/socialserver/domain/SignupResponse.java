package com.github.prgrms.socialserver.domain;

public class SignupResponse {

    private final boolean success;
    private final String response;

    public SignupResponse(boolean success, String response) {
        this.success = success;
        this.response = response;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getResponse() {
        return response;
    }
}
