package com.github.prgrms.socialserver.domain;

import java.util.Objects;

public class Email {
    private final String email;

    public Email(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Email emailTwin = (Email) obj;
        return Objects.equals(email, emailTwin.email);
    }
}
