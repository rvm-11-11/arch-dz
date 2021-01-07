package rvm.dz.dz6users.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import rvm.dz.dz6users.repositories.NotificationEntity;
import rvm.dz.dz6users.repositories.NotificationRepository;

@RestController
@Slf4j
@RequiredArgsConstructor
public class NotificationsController {

    @Autowired
    private KafkaTemplate<String, String> template;
    ObjectMapper objectMapper = new ObjectMapper();

    private final NotificationRepository repository;

    @GetMapping("/notifications")
    public ResponseEntity getAllNotifications() {
        return ResponseEntity.ok(repository.findAll());
    }

    @KafkaListener(topics = "orderProcessed")
    public void listenOrderProcessed(ConsumerRecord<?, ?> cr) throws Exception {
        OrderProcessedEvent event = objectMapper.readValue((String) cr.value(), OrderProcessedEvent.class);
        UserEntity user = getUserByUserId(event.userId);
        String message;
        if (event.status == OrderProcessedEvent.Status.PAYMENT_ACCEPTED) {
            message = "Dear " + user.getName() + "! You payment has been approved and order will be delivered soon!";
        } else {
            message = "Dear " + user.getName() + "! Unfortunately your payment has been rejected, please check your balance and try again!";
        }
        NotificationEntity notificationEntity = NotificationEntity.builder()
                .userId(event.getUserId())
                .email(user.getEmail())
                .message(message)
                .orderId(event.orderId).build();
        repository.save(notificationEntity);
    }

    private UserEntity getUserByUserId(Long userId) {
        RestTemplate restTemplate = new RestTemplate();
        String userResourceUrl= "http://localhost:8081/users?id=" + userId;
        UserEntity user = restTemplate.getForObject(userResourceUrl, UserEntity.class);
        return user;
    }

}
