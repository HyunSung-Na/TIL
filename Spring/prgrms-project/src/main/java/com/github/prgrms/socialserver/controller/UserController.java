package com.github.prgrms.socialserver.controller;

import com.github.prgrms.socialserver.domain.SignupRequest;
import com.github.prgrms.socialserver.domain.User;
import com.github.prgrms.socialserver.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/users")
    public List<User> retrieveAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/api/users/{seq}")
    public User retrieveUser(@PathVariable Long seq) {
        return userService.findOne(seq);
    }

    @PostMapping("/api/users/join")
    public User createUser(@Valid @RequestBody SignupRequest signupRequest) {
        User signUp =  signupRequest.toEntity();
        return userService.save(signUp);
    }

    @PostMapping("/api/users/delete/{email}")
    public void deleteUser(@PathVariable String email) {
        userService.delete(email);
    }
}
