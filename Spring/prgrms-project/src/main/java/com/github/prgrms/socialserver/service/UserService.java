package com.github.prgrms.socialserver.service;

import com.github.prgrms.socialserver.domain.Email;
import com.github.prgrms.socialserver.domain.SignupRequest;
import com.github.prgrms.socialserver.domain.User;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface UserService {

    List<User> findAll() throws DataAccessException;

    User findOne(Email email) throws DataAccessException;

    User save(SignupRequest signupRequest) throws DataAccessException;

    void delete(Email email) throws DataAccessException;
}
