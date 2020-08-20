package com.github.prgrms.socialserver.domain;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class SignupRequest {

    @NotEmpty(message = "email is required")
    @Email
    private String principal;

    @NotEmpty(message = "password is required")
    private String credentials;

    public SignupRequest(User user) {
        this.principal = user.getEmail();
        this.credentials = user.getPasswd();
    }


    public SignupRequest(String principal, String credentials) {
        this.principal = principal;
        this.credentials = credentials;
    }

    public String getPrincipal() {
        String principal = this.principal;
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    public User toEntity() {
        return new User.Builder()
                .email(principal)
                .passwd(credentials)
                .build();
    }
}
