package com.github.prgrms.socialserver.service;

import com.github.prgrms.socialserver.domain.Email;
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
    public User findOne(Email email) throws DataAccessException {
        User user = userRepository.findOne(email);
        return user;
    }

    @Override
    public User save(@Valid SignupRequest signupRequest) throws DataAccessException {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        LocalDateTime timestamp = LocalDateTime.now();

        User user = new User.Builder()
                .seq((Long) keyHolder.getKey())
                .email(signupRequest.getPrincipal())
                .passwd(signupRequest.getCredentials())
                .login_count(0)
                .last_login_at(timestamp)
                .create_at(timestamp)
                .build();

        return userRepository.save(user);
    }

    @Override
    public void delete(Email email) throws DataAccessException {
        userRepository.delete(email);
    }
}
