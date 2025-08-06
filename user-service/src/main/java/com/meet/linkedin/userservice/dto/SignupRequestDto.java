package com.meet.linkedin.userservice.dto;

import lombok.Data;

@Data
public class SignupRequestDto {
    private Long id;
    private String name;
    private String email;
    private String password;
}
