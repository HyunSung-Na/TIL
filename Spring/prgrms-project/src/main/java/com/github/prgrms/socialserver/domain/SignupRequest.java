package com.github.prgrms.socialserver.domain;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class SignupRequest {

    @NotEmpty(message = "email is required")
    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "Invalid email")
    private Email principal;

    @NotEmpty(message = "password is required")
    private String credentials;

    public Email getPrincipal() {
        Email principal = this.principal;
        return principal;
    }

    public void setPrincipal(Email principal) {
        this.principal = principal;
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }
}
