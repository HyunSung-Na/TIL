package com.github.prgrms.socialserver.service;

import com.github.prgrms.socialserver.domain.SignupRequest;
import com.github.prgrms.socialserver.domain.User;
import com.github.prgrms.socialserver.repository.UserRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAll() throws DataAccessException {
        List<User> users = userRepository.findAll();
        return users;
    }

    @Override
    public User findOne(Long seq) throws DataAccessException {
        User user = userRepository.findOne(seq);
        return user;
    }

    @Override
    public User save(@Valid User user) throws DataAccessException {
        return userRepository.save(user);
    }

    @Override
    public void delete(String email) throws DataAccessException {
        userRepository.delete(email);
    }
}
