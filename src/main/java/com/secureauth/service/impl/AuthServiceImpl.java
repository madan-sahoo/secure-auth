package com.secureauth.service.impl;

import com.secureauth.dto.request.LoginRequest;
import com.secureauth.dto.response.LoginResponse;
import com.secureauth.entity.User;
import com.secureauth.exception.InvalidCredentialsException;
import com.secureauth.exception.UserNotFoundException;
import com.secureauth.repository.UserRepository;
import com.secureauth.service.AuthService;
import com.secureauth.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.secureauth.util.Constant.*;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, JwtService jwtService,
                           PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.jwtService = jwtService;

        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // find the user
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        // match the password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash()))
            throw new InvalidCredentialsException(INVALID_USERNAME_OR_PASSWORD);

        // generate token and return
        String token = jwtService.generateToken(user);
        return new LoginResponse(token, TOKEN_TYPE_BEARER);
    }
}