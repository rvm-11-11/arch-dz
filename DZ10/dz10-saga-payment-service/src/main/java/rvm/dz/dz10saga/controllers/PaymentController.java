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
import rvm.dz.dz10saga.repositories.Event;
import rvm.dz.dz10saga.repositories.PaymentEntity;
import rvm.dz.dz10saga.repositories.PaymentRepostitory;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Long.parseLong;
import static rvm.dz.dz10saga.repositories.PaymentEntity.Status.ROLLED_BACK;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentRepostitory paymentRepostitory;

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
                if(paymentRepostitory.findByOrderId(incomingEvent.getOrderId()).isEmpty()) {
                    if (parseLong(incomingEvent.getEventData().get("price")) < 5000) {
                        PaymentEntity createdPayment = paymentRepostitory.save(PaymentEntity.builder()
                                .orderId(incomingEvent.getOrderId())
                                .price(parseLong(incomingEvent.getEventData().get("price")))
                                .paymentStatus(PaymentEntity.Status.APPROVED)
                                .build());

                        Event outgoingEvent = Event.builder()
                                .orderId(createdPayment.getOrderId())
                                .eventType(Event.EventType.PAYMENT_APPROVED)
                                .build();

                        this.template.send("shopping-events", objectMapper.writeValueAsString(outgoingEvent));

                    } else {
                        PaymentEntity createdPayment = paymentRepostitory.save(PaymentEntity.builder()
                                .orderId(incomingEvent.getOrderId())
                                .price(parseLong(incomingEvent.getEventData().get("price")))
                                .paymentStatus(PaymentEntity.Status.REJECTED)
                                .build());

                        Event outgoingEvent = Event.builder()
                                .orderId(createdPayment.getOrderId())
                                .eventType(Event.EventType.PAYMENT_REJECTED)
                                .build();

                        this.template.send("shopping-events", objectMapper.writeValueAsString(outgoingEvent));
                    }
                }
                break;
            case ORDER_REJECTED:
                paymentRepostitory.findByOrderId(incomingEvent.getOrderId()).ifPresentOrElse(paymentEntity -> {
                            log.info("Returning money to customer: " + paymentEntity.getPrice());
                            paymentEntity.setPaymentStatus(ROLLED_BACK);
                            paymentRepostitory.save(paymentEntity);
                        }, () -> {
                            PaymentEntity createdPayment = paymentRepostitory.save(PaymentEntity.builder()
                                    .orderId(incomingEvent.getOrderId())
                                    .price(parseLong(incomingEvent.getEventData().get("price")))
                                    .paymentStatus(PaymentEntity.Status.REJECTED)
                                    .build());
                        }
                );
                break;
            default:
                log.info("event ignored " + incomingEvent);
        }
    }

    @GetMapping("/payments")
    public ResponseEntity getAllPayments() {
        return ResponseEntity.ok(paymentRepostitory.findAll());
    }

    @RequestMapping("/health")
    public Map<String, String> health() {
        log.info("Calling health()");
        Map<String, String> response = new HashMap();
        response.put("status", "OK");
        return response;
    }
}
