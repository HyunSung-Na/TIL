package com.github.prgrms.socialserver.repository;

import com.github.prgrms.socialserver.domain.Email;
import com.github.prgrms.socialserver.domain.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Repository
public class UserRowMapper implements RowMapper {


    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Timestamp lastLogin = resultSet.getTimestamp("last_login_at");
        LocalDateTime localDateTime = lastLogin == null ? null : lastLogin.toLocalDateTime();
        User user = new User.Builder()
                .seq(resultSet.getLong("seq"))
                .email(resultSet.getObject("email", Email.class))
                .passwd(resultSet.getString("passwd"))
                .login_count(resultSet.getInt("login_count"))
                .last_login_at(localDateTime)
                .create_at(resultSet.getTimestamp("create_at").toLocalDateTime())
                .build();
        return user;
    }
}
