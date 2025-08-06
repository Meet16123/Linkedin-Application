package com.meet.linkedin.notification_service.consumer;

import com.meet.linkedin.connections_service.event.AcceptConnectionRequestEvent;
import com.meet.linkedin.connections_service.event.SendConnectionRequestEvent;
import com.meet.linkedin.notification_service.service.SendNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConnectionsServiceConsumer {

    private final SendNotification sendNotification;

    @KafkaListener(topics = "send-connection-request-topic")
    public void handleSendConnectionRequestEvent(SendConnectionRequestEvent sendConnectionRequestEvent) {
        log.info("Handling send connection request event");
        String message = "You have received a connection request from " + sendConnectionRequestEvent.getSenderId();

        sendNotification.send(sendConnectionRequestEvent.getReceiverId(), message);
    }

    @KafkaListener(topics = "accept-connection-request-topic")
    public void handleAcceptConnectionRequestEvent(AcceptConnectionRequestEvent acceptConnectionRequestEvent) {
        log.info("Handling accept connection request event");
        String message = "Your connection request to " + acceptConnectionRequestEvent.getReceiverId() + " has been accepted";

        sendNotification.send(acceptConnectionRequestEvent.getSenderId(), message);
    }
}
