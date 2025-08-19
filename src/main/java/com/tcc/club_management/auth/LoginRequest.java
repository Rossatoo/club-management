package com.tcc.club_management.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String senha;
}
