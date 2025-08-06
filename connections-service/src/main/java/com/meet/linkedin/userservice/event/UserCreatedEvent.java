package com.meet.linkedin.userservice.event;

import lombok.Data;

@Data
public class UserCreatedEvent {
    private String name;
    private  Long user_id;
}