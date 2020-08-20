package com.github.prgrms.socialserver.repository;

import com.github.prgrms.socialserver.domain.User;
import org.springframework.dao.DataAccessException;

import javax.validation.Valid;
import java.util.List;

public interface UserRepository {


    boolean existsByEmail(String principal);


    List<User> findAll() throws DataAccessException;

    User findOne(Long seq) throws DataAccessException;

    User save(@Valid User user) throws DataAccessException;

    void delete(String email) throws DataAccessException;

}
