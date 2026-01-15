package com.secureauth.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String name;
    private Date birthday;
}