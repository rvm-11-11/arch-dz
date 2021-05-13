package rvm.dz.dz10saga.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import rvm.dz.dz10saga.repositories.DeliveryEntity;
import rvm.dz.dz10saga.repositories.DeliveryRepository;
import rvm.dz.dz10saga.repositories.Event;

import java.time.Instant;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Long.parseLong;
import static rvm.dz.dz10saga.repositories.DeliveryEntity.Status.ROLLED_BACK;

@RestController
@Slf4j
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryRepository deliveryRepository;

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
                Instant deliveryDate = Instant.ofEpochSecond(parseLong(incomingEvent.getEventData().get("deliveryDate")));
                if(deliveryRepository.findByOrderId(incomingEvent.getOrderId()).isEmpty()) {
                    Instant earliestPossibleDeliveryDate = Instant.now().plus(30, ChronoUnit.DAYS);
                    if (deliveryDate.isAfter(earliestPossibleDeliveryDate)) {
                        DeliveryEntity createdDelivery = deliveryRepository.save(DeliveryEntity.builder()
                                .orderId(incomingEvent.getOrderId())
                                .deliveryDate(deliveryDate)
                                .deliveryStatus(DeliveryEntity.Status.APPROVED)
                                .build());

                        Event outgoingEvent = Event.builder()
                                .orderId(createdDelivery.getOrderId())
                                .eventType(Event.EventType.DELIVERY_APPROVED)
                                .build();

                        this.template.send("shopping-events", objectMapper.writeValueAsString(outgoingEvent));

                    } else {
                        DeliveryEntity createdDelivery = deliveryRepository.save(DeliveryEntity.builder()
                                .orderId(incomingEvent.getOrderId())
                                .deliveryDate(deliveryDate)
                                .deliveryStatus(DeliveryEntity.Status.REJECTED)
                                .build());

                        Event outgoingEvent = Event.builder()
                                .orderId(createdDelivery.getOrderId())
                                .eventType(Event.EventType.DELIVERY_REJECTED)
                                .build();

                        this.template.send("shopping-events", objectMapper.writeValueAsString(outgoingEvent));
                    }
                }
                break;
            case ORDER_REJECTED:
//                deliveryDate = Instant.ofEpochSecond(parseLong(incomingEvent.getEventData().get("deliveryDate")));
                deliveryRepository.findByOrderId(incomingEvent.getOrderId()).ifPresentOrElse(deliveryEntity -> {
                            log.info("Returning time slot as available: " + deliveryEntity.getDeliveryDate());
                            deliveryEntity.setDeliveryStatus(ROLLED_BACK);
                            deliveryRepository.save(deliveryEntity);
                        }, () -> {
                                DeliveryEntity createdPayment = deliveryRepository.save(DeliveryEntity.builder()
                                    .orderId(incomingEvent.getOrderId())
//                                    .deliveryDate(deliveryDate)
                                    .deliveryStatus(DeliveryEntity.Status.REJECTED)
                                    .build());
                        }
                );
                break;
            default:
                log.info("event ignored " + incomingEvent);
        }
    }

    @GetMapping("/deliveries")
    public ResponseEntity getAllDeliveries() {
        return ResponseEntity.ok(deliveryRepository.findAll());
    }

    @RequestMapping("/health")
    public Map<String, String> health() {
        log.info("Calling health()");
        Map<String, String> response = new HashMap();
        response.put("status", "OK");
        return response;
    }
}
