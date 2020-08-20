package com.github.prgrms.socialserver.service;

import com.github.prgrms.socialserver.domain.SignupRequest;
import com.github.prgrms.socialserver.domain.User;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface UserService {

    List<User> findAll() throws DataAccessException;

    User findOne(Long seq) throws DataAccessException;

    User save(User user) throws DataAccessException;

    void delete(String email) throws DataAccessException;
}
