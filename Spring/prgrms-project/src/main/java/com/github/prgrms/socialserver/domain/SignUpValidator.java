package com.github.prgrms.socialserver.domain;

import com.github.prgrms.socialserver.controller.SignupRequest;
import com.github.prgrms.socialserver.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class SignUpValidator implements Validator {

    private final UserRepository userRepository;

    public SignUpValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(SignupRequest.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        SignupRequest signupRequest =(SignupRequest) o;

        if (userRepository.existsByEmail(signupRequest.getPrincipal())) {
            errors.rejectValue("email", "invalid.email", new Object[]{signupRequest.getPrincipal()}, "이미 사용중인 이메일 입니다.");
        }

    }
}
