package com.github.prgrms.socialserver.repository;

import com.github.prgrms.socialserver.domain.Email;
import com.github.prgrms.socialserver.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class UserRepositoryImpl implements UserRepository {

    private JdbcTemplate jdbcTemplate;

    RowMapper<User> userRowMapper;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public boolean existsByEmail(Email principal) {
        if (jdbcTemplate.queryForObject("SELECT email FROM USERS WHERE email=?", userRowMapper, principal) != null)
            return true;
        return false;
    }

    @Override
    public List<User> findAll() throws DataAccessException {
        List<User> users = jdbcTemplate.query("SELECT seq, email from USERS", userRowMapper);
        return users;
    }

    @Override
    public User findOne(Email email) throws DataAccessException {
        return (User)jdbcTemplate.queryForObject("SELECT seq, email FROM USERS WHERE email=?", userRowMapper, email);
    }


    @Override
    @Transactional(readOnly = false)
    public User save(User user) throws DataAccessException {
        this.jdbcTemplate.update("INSERT INTO USERS(email, passwd, login_count, last_login_at, create_at) VALUES ( ?, ?, ?, ?, ? )", userRowMapper);
        return user;
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(Email email) throws DataAccessException {
        this.jdbcTemplate.update("DELETE FROM USERS WHERE EMAIL = ?", email);
    }
}
