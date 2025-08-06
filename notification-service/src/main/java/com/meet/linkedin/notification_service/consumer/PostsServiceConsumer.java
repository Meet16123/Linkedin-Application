package com.meet.linkedin.notification_service.consumer;

import com.meet.linkedin.notification_service.clients.ConnectionsClient;
import com.meet.linkedin.notification_service.dto.PersonDto;
import com.meet.linkedin.notification_service.service.SendNotification;
import com.meet.linkedin.post_service.events.PostCreatedEvent;
import com.meet.linkedin.post_service.events.PostLikedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostsServiceConsumer {

    private final ConnectionsClient connectionsClient;
    private final SendNotification sendNotification;

    @KafkaListener(topics = "post-created-topic")
    public void handlePostCreated(PostCreatedEvent postCreatedEvent) {
        log.info("Sending notifications: handlePostCreated: {}", postCreatedEvent);
        List<PersonDto> connections = connectionsClient.getFirstDegreeConnections(postCreatedEvent.getCreatorId());

        for(PersonDto connection: connections) {
            log.info("Sending notification to: {}", connection.getName());
            sendNotification.send(connection.getUserId(), "Your connection: " + postCreatedEvent.getCreatorId() + " has created a post, Check it out!");
        }
    }

    @KafkaListener(topics = "post-liked-topic")
    public void handlePostLiked(PostLikedEvent postLikedEvent) {
        log.info("Sending notifications: handlePostLiked: {}", postLikedEvent);

        String message = String.format("Your Post, %d has been liked by %d", postLikedEvent.getPostId() , postLikedEvent.getLikedByUserId());

        sendNotification.send(postLikedEvent.getCreatorId(), message);
    }

}
