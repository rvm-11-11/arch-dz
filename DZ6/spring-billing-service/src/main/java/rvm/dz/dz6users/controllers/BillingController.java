package rvm.dz.dz6users.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import rvm.dz.dz6users.repositories.AccountEntity;
import rvm.dz.dz6users.repositories.AccountRepository;

@RestController
@Slf4j
@RequiredArgsConstructor
public class BillingController {

    @Autowired
    private KafkaTemplate<String, String> template;
    ObjectMapper objectMapper = new ObjectMapper();

    private final AccountRepository repository;

    @GetMapping("/accounts")
    public ResponseEntity getAllAccounts() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping("/deposit")
    public ResponseEntity deposit(@RequestBody DepositRequest request) {
        AccountEntity account = repository.findByUserId(request.userId).get();
        account.setBalance(account.getBalance() + request.getSum());
        repository.save(account);

        return ResponseEntity.ok().build();
    }

    @KafkaListener(topics = "userCreated")
    public void listenUserCreated(ConsumerRecord<?, ?> cr) throws Exception {
        UserCreatedEvent event = objectMapper.readValue((String) cr.value(), UserCreatedEvent.class);
        repository.save(AccountEntity.builder().balance(0L).userId(event.getId()).build());
    }

    @KafkaListener(topics = "orderCreated")
    public ResponseEntity listenOrderCreated(ConsumerRecord<?, ?> cr) throws JsonProcessingException {
        OrderCreatedEvent incomingEvent = objectMapper.readValue((String) cr.value(), OrderCreatedEvent.class);

        AccountEntity account = repository.findByUserId(incomingEvent.userId).get();
        if(account.getBalance() >= incomingEvent.getSum()) {
            account.setBalance(account.getBalance() - incomingEvent.getSum());
            repository.save(account);
            OrderProcessedEvent outcomingEvent =
                    new OrderProcessedEvent(OrderProcessedEvent.Status.PAYMENT_ACCEPTED, incomingEvent.getOrderId());
            this.template.send( "orderProcessed", objectMapper.writeValueAsString(outcomingEvent));
        } else {
            OrderProcessedEvent outcomingEvent =
                    new OrderProcessedEvent(OrderProcessedEvent.Status.PAYMENT_REJECTED, incomingEvent.getOrderId());
            this.template.send( "orderProcessed", objectMapper.writeValueAsString(outcomingEvent));
        }

        return ResponseEntity.ok().build();
    }

}
