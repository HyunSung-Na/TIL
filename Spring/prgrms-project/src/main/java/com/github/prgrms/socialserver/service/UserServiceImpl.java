package com.github.prgrms.socialserver.service;

import com.github.prgrms.socialserver.domain.Email;
import com.github.prgrms.socialserver.domain.User;
import com.github.prgrms.socialserver.repository.UserRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

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
    public User save(Email email, String password) throws DataAccessException {
        checkArgument(isNotEmpty(password), "password must be provided.");
        checkArgument(
                password.length() >= 4 && password.length() <= 15,
                "password length must be between 4 and 15 characters."
        );

        User user = new User(email, password);
        return userRepository.save(user);
    }

    @Override
    public void delete(String email) throws DataAccessException {
        userRepository.delete(email);
    }
}
