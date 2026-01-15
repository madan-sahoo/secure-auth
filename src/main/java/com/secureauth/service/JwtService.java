package com.secureauth.service;

import com.secureauth.entity.User;

public interface JwtService {
    String generateToken(User user);
    boolean validateToken(String token);
    String extractUsername(String token);
}