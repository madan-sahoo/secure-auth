package com.secureauth.dto.response;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginResponse {
    private String accessToken;
    private String tokenType;
}
