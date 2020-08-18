package com.github.prgrms.socialserver.controller;

import com.github.prgrms.socialserver.domain.Email;
import com.github.prgrms.socialserver.domain.SignupRequest;
import com.github.prgrms.socialserver.domain.User;
import com.github.prgrms.socialserver.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("api/users")
    public List<User> retrieveAllUsers() {
        return userService.findAll();
    }

    @GetMapping("api/users/{email}")
    public User retrieveUser(@PathVariable Email email) {
        return userService.findOne(email);
    }

    @PostMapping("api/users/join")
    public void createUser(@Valid @RequestBody SignupRequest signupRequest) {
        User savedUser = userService.save(signupRequest);
    }

    @PostMapping("api/users/delete/{email}")
    public void deleteUser(@PathVariable Email email) {
        userService.delete(email);
    }
}
