package com.github.prgrms.socialserver.domain;

import java.time.LocalDateTime;

public class SignupResponse {

    private Email email;

    private String passwd;

    private int login_count;

    private LocalDateTime last_login_at;

    private LocalDateTime create_at;

    public Email getEmail() {
        return email;
    }

    public String getPasswd() {
        return passwd;
    }

    public int getLogin_count() {
        return login_count;
    }

    public LocalDateTime getLast_login_at() {
        return last_login_at;
    }

    public LocalDateTime getCreate_at() {
        return create_at;
    }
}
