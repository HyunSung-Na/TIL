package com.github.prgrms.socialserver.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.junit.platform.commons.util.ToStringBuilder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Objects;

public class User {

    private final Long seq;

    private final String email;

    @JsonIgnore
    private String passwd;

    private int login_count;

    private LocalDateTime last_login_at;

    private final LocalDateTime create_at;

    public User(long seq, String email, String passwd, int login_count, Date last_login_at, Date create_at){
        this(null, email, passwd, 0, null, null);
    }

    public User(Long seq, String email, String passwd, int login_count, LocalDateTime last_login_at, LocalDateTime create_at) {

        this.seq = seq;
        this.email = email;
        this.passwd = passwd;
        this.login_count = login_count;
        this.last_login_at = last_login_at;
        this.create_at = create_at;
    }

    public void afterLoginSuccess() {
         login_count++;
         last_login_at = LocalDateTime.now();
    }

    public Long getSeq() {
        return seq;
    }

    public String getEmail() {
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

    public int hashCode(int hash) {
        return hashCode(Objects.hash(seq));
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(seq, user.seq);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("seq", seq)
                .append("email", email)
                .append("passwd", passwd)
                .append("login_count", login_count)
                .append("last_login_at", last_login_at)
                .append("create_at", create_at)
                .toString();
    }


    public static class Builder {
        private Long seq;
        private String email;
        private String passwd;
        private int login_count;
        private LocalDateTime last_login_at;
        private LocalDateTime create_at;

        public Builder() {
        }

        public Builder(User user){
            this.seq = user.seq;
            this.email = user.email;
            this.passwd = user.passwd;
            this.login_count = user.login_count;
            this.last_login_at = user.last_login_at;
            this.create_at = user.create_at;
        }

        public Builder seq(Long seq) {
            this.seq = seq;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder passwd(String passwd) {
            this.passwd = passwd;
            return this;
        }

        public Builder login_count(int login_count) {
            this.login_count = login_count;
            return this;
        }

        public Builder last_login_at(LocalDateTime last_login_at) {
            this.last_login_at = last_login_at;
            return this;
        }

        public Builder create_at(LocalDateTime create_at) {
            this.create_at = create_at;
            return this;
        }

        public User build() {
            return new User(seq, email, passwd, login_count, last_login_at, create_at);}
    }

}
