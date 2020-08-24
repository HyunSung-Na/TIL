package com.github.prgrms.socialserver.controller;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SignupRequest {

    private String principal;

    private String credentials;

    protected SignupRequest() {}

    public String getPrincipal() {
        return principal;
    }

    public String getCredentials() {
        return credentials;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("principal", principal)
                .append("credentials", credentials)
                .toString();
    }

}
