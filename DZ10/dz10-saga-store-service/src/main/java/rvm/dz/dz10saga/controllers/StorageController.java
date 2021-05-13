package rvm.dz.dz10saga.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import rvm.dz.dz10saga.repositories.Event;
import rvm.dz.dz10saga.repositories.StorageEntity;
import rvm.dz.dz10saga.repositories.StorageRepostitory;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Long.parseLong;
import static rvm.dz.dz10saga.repositories.StorageEntity.Status.ROLLED_BACK;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StorageController {

    private final StorageRepostitory storageRepostitory;

    @Autowired
    private KafkaTemplate<String, String> template;

    ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "shopping-events")
    public void listen(ConsumerRecord<?, ?> cr) throws Exception {
        log.info(cr.toString());
        Event event = new ObjectMapper().readValue((String)cr.value(), Event.class);
        applyEvent(event);
    }

    @SneakyThrows
    private void applyEvent(Event incomingEvent) {
        switch (incomingEvent.getEventType()) {
            case ORDER_PENDING:
                if(storageRepostitory.findByOrderId(incomingEvent.getOrderId()).isEmpty()) {
                    if (parseLong(incomingEvent.getEventData().get("itemId")) > 3) {
                        StorageEntity createdPayment = storageRepostitory.save(StorageEntity.builder()
                                .orderId(incomingEvent.getOrderId())
                                .itemId(parseLong(incomingEvent.getEventData().get("itemId")))
                                .storeStatus(StorageEntity.Status.APPROVED)
                                .build());

                        Event outgoingEvent = Event.builder()
                                .orderId(createdPayment.getOrderId())
                                .eventType(Event.EventType.STORE_RESERVATION_APPROVED)
                                .build();

                        this.template.send("shopping-events", objectMapper.writeValueAsString(outgoingEvent));

                    } else {
                        StorageEntity createdPayment = storageRepostitory.save(StorageEntity.builder()
                                .orderId(incomingEvent.getOrderId())
                                .itemId(parseLong(incomingEvent.getEventData().get("itemId")))
                                .storeStatus(StorageEntity.Status.REJECTED)
                                .build());

                        Event outgoingEvent = Event.builder()
                                .orderId(createdPayment.getOrderId())
                                .eventType(Event.EventType.STORE_RESERVATION_REJECTED)
                                .build();

                        this.template.send("shopping-events", objectMapper.writeValueAsString(outgoingEvent));
                    }
                }
                break;
            case ORDER_REJECTED:
                storageRepostitory.findByOrderId(incomingEvent.getOrderId()).ifPresentOrElse(storageEntity -> {
                            log.info("Unblocking the item: " + storageEntity.getItemId());
                            storageEntity.setStoreStatus(ROLLED_BACK);
                            storageRepostitory.save(storageEntity);
                        }, () -> {
                            StorageEntity createdPayment = storageRepostitory.save(StorageEntity.builder()
                                    .orderId(incomingEvent.getOrderId())
                                    .itemId(parseLong(incomingEvent.getEventData().get("itemId")))
                                    .storeStatus(StorageEntity.Status.REJECTED)
                                    .build());
                        }
                );
                break;
            default:
                log.info("event ignored " + incomingEvent);
        }
    }

    @GetMapping("/stores")
    public ResponseEntity getAllStores() {
        return ResponseEntity.ok(storageRepostitory.findAll());
    }

    @RequestMapping("/health")
    public Map<String, String> health() {
        log.info("Calling health()");
        Map<String, String> response = new HashMap();
        response.put("status", "OK");
        return response;
    }
}
