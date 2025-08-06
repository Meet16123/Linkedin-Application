package com.meet.linkedin.userservice.event;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserCreatedEvent {
    private String name;
    private  Long user_id;
}