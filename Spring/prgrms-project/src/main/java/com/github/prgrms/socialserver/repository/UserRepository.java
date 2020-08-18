package com.github.prgrms.socialserver.repository;

import com.github.prgrms.socialserver.domain.Email;
import com.github.prgrms.socialserver.domain.User;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface UserRepository {


    boolean existsByEmail(Email principal);


    List<User> findAll() throws DataAccessException;

    User findOne(Email email) throws DataAccessException;

    User save(User user) throws DataAccessException;

    void delete(Email email) throws DataAccessException;

}
