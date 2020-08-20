package com.github.prgrms.socialserver.repository;

import com.github.prgrms.socialserver.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import javax.validation.Valid;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class UserRepositoryImpl implements UserRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public boolean existsByEmail(String principal) {
        if (jdbcTemplate.queryForObject("SELECT email FROM USERS WHERE email=?", userRowMapper(), principal) != null)
            return true;
        return false;
    }

    @Override
    public List<User> findAll() throws DataAccessException {
        List<User> users = jdbcTemplate.query("SELECT * from USERS", userRowMapper());
        return users;
    }

    @Override
    public User findOne(Long seq) throws DataAccessException {
        return (User)jdbcTemplate.queryForObject("SELECT * FROM USERS WHERE SEQ=?", userRowMapper(), seq);
    }


    @Override
    @Transactional(readOnly = false)
    public User save(@Valid User user) throws DataAccessException {
        final String query =
                "insert into users (seq ,email, passwd, login_count, last_login_at, create_at)" +
                        " values(?, ?, ?, ?, ?, ?)";

        return (User)jdbcTemplate.queryForObject(query, userRowMapper());

    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String email) throws DataAccessException {
        this.jdbcTemplate.update("DELETE FROM USERS WHERE EMAIL = ?", email);
    }

    private static RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            Timestamp lastLogin = rs.getTimestamp("last_login_at");
            LocalDateTime localDateTime = lastLogin == null ? null : lastLogin.toLocalDateTime();
            User user = new User.Builder()
                    .seq(rs.getLong(String.valueOf(keyHolder)))
                    .email(rs.getString("email"))
                    .passwd(rs.getString("passwd"))
                    .login_count(rs.getInt("login_count"))
                    .last_login_at(localDateTime)
                    .create_at(rs.getTimestamp("create_at").toLocalDateTime())
                    .build();
            return user;
        };
    }
}


