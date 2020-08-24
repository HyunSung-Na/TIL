package com.github.prgrms.socialserver.repository;

import com.github.prgrms.socialserver.domain.Email;
import com.github.prgrms.socialserver.domain.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import javax.validation.Valid;
import java.sql.PreparedStatement;
import java.util.List;

import static com.github.prgrms.socialserver.util.DateTimeUtils.dateTimeOf;
import static com.github.prgrms.socialserver.util.DateTimeUtils.timestampOf;

@Repository
@Transactional(readOnly = true)
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepositoryImpl (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
                        " values(null, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn ->{
            PreparedStatement ps = conn.prepareStatement(query, new String[]{"seq"});
            ps.setString(1, user.getEmail().getAddress());
            ps.setString(2, user.getPasswd());
            ps.setInt(3, user.getLogin_count());
            ps.setTimestamp(4, timestampOf(user.getLast_login_at().orElse(null)));
            ps.setTimestamp(5, timestampOf(user.getCreate_at()));
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        long generatedSeq= key != null ? key.longValue() : -1;
        return new User.Builder(user)
                .seq(generatedSeq)
                .build();
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String email) throws DataAccessException {
        this.jdbcTemplate.update("DELETE FROM USERS WHERE EMAIL = ?", email);
    }

    private static RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            User user = new User.Builder()
                    .seq(rs.getLong("seq"))
                    .email(new Email(rs.getString("email")))
                    .passwd(rs.getString("passwd"))
                    .login_count(rs.getInt("login_count"))
                    .last_login_at(dateTimeOf(rs.getTimestamp("last_login_at")))
                    .create_at(dateTimeOf(rs.getTimestamp("create_at")))
                    .build();
            return user;
        };
    }
}


