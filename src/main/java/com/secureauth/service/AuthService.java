package com.secureauth.service;

import com.secureauth.dto.request.LoginRequest;
import com.secureauth.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
}
