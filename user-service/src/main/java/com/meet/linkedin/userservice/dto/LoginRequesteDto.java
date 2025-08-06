package com.meet.linkedin.userservice.dto;

import lombok.Data;

@Data
public class LoginRequesteDto {
    private String email;
    private String password;
}
