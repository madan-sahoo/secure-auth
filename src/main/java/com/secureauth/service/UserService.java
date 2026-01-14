package com.secureauth.service;

import com.secureauth.dto.request.RegisterRequest;
import com.secureauth.dto.response.RegisterResponse;

public interface UserService {
    RegisterResponse register(RegisterRequest registerRequest);
}
