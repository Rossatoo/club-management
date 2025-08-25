package com.tcc.club_management.auth;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String email;
    private String senha;
}
