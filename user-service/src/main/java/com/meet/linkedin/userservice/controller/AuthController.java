package com.meet.linkedin.userservice.controller;

import com.meet.linkedin.userservice.dto.LoginRequesteDto;
import com.meet.linkedin.userservice.dto.SignupRequestDto;
import com.meet.linkedin.userservice.dto.UserDto;
import com.meet.linkedin.userservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignupRequestDto signupRequestDto) {
        UserDto userDto = authService.signUp(signupRequestDto);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequesteDto loginRequesteDto) {
        String token = authService.login(loginRequesteDto);
        log.info("User logged in successfully");
        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}
