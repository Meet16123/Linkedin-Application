package com.meet.linkedin.userservice.service;

import com.meet.linkedin.userservice.dto.LoginRequesteDto;
import com.meet.linkedin.userservice.dto.SignupRequestDto;
import com.meet.linkedin.userservice.dto.UserDto;
import com.meet.linkedin.userservice.entity.User;
import com.meet.linkedin.userservice.event.UserCreatedEvent;
import com.meet.linkedin.userservice.exception.BadRequestException;
import com.meet.linkedin.userservice.exception.ResourceNotFoundException;
import com.meet.linkedin.userservice.repository.UserRepository;
import com.meet.linkedin.userservice.utill.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;
    private final KafkaTemplate<Long, UserCreatedEvent> userCreatedEventKafkaTemplate;


    public UserDto signUp(SignupRequestDto signupRequestDto) {
        boolean exists = userRepository.existsByEmail(signupRequestDto.getEmail());
        if(exists) {
            throw new BadRequestException("User already exists, cannot signup again.");
        }

        User user = modelMapper.map(signupRequestDto, User.class);
        user.setPassword(PasswordUtil.hashPassword(signupRequestDto.getPassword()));

        User savedUser = userRepository.save(user);
        //Send User Created Event
        UserCreatedEvent userCreatedEvent = UserCreatedEvent.builder()
                .name(savedUser.getName())
                .user_id(savedUser.getId())
                .build();
        userCreatedEventKafkaTemplate.send("user-created-topic", userCreatedEvent);

        return modelMapper.map(savedUser, UserDto.class);
    }

    public String login(LoginRequesteDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: "+loginRequestDto.getEmail()));

        boolean isPasswordMatch = PasswordUtil.checkPassword(loginRequestDto.getPassword(), user.getPassword());

        if(!isPasswordMatch) {
            throw new BadRequestException("Incorrect password");
        }

        return jwtService.generateAccessToken(user);
    }
}