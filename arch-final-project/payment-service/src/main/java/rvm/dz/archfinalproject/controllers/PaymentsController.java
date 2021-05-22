package rvm.dz.archfinalproject.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import rvm.dz.archfinalproject.repositories.Event;
import rvm.dz.archfinalproject.repositories.PaymentEntity;
import rvm.dz.archfinalproject.repositories.PaymentsRepository;

import java.util.Map;

import static java.lang.Long.parseLong;
import static rvm.dz.archfinalproject.repositories.PaymentEntity.Status.ROLLED_BACK;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PaymentsController {

    private final PaymentsRepository paymentRepository;
    private final KafkaTemplate<String, String> template;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "travel-agency-events")
    public void listen(ConsumerRecord<?, ?> cr) throws Exception {
        log.info(cr.toString());
        Event event = new ObjectMapper().readValue((String)cr.value(), Event.class);
        applyEvent(event);
    }

    @SneakyThrows
    private void applyEvent(Event incomingEvent) {
        switch (incomingEvent.getEventType()) {
            case ORDER_PENDING:
                if(paymentRepository.findByOrderId(incomingEvent.getOrderId()).isEmpty()) {
                    if (parseLong(incomingEvent.getEventData().get("price")) < 5000) {
                        PaymentEntity createdPayment = paymentRepository.save(PaymentEntity.builder()
                                .orderId(incomingEvent.getOrderId())
                                .price(parseLong(incomingEvent.getEventData().get("price")))
                                .paymentStatus(PaymentEntity.Status.APPROVED)
                                .build());

                        Event outgoingEvent = Event.builder()
                                .orderId(createdPayment.getOrderId())
                                .eventType(Event.EventType.PAYMENT_APPROVED)
                                .build();

                        this.template.send("travel-agency-events", objectMapper.writeValueAsString(outgoingEvent));

                    } else {
                        PaymentEntity createdPayment = paymentRepository.save(PaymentEntity.builder()
                                .orderId(incomingEvent.getOrderId())
                                .price(parseLong(incomingEvent.getEventData().get("price")))
                                .paymentStatus(PaymentEntity.Status.REJECTED)
                                .build());

                        Event outgoingEvent = Event.builder()
                                .orderId(createdPayment.getOrderId())
                                .eventType(Event.EventType.PAYMENT_REJECTED)
                                .build();

                        this.template.send("travel-agency-events", objectMapper.writeValueAsString(outgoingEvent));
                    }
                }
                break;
            case ORDER_REJECTED:
                paymentRepository.findByOrderId(incomingEvent.getOrderId()).ifPresentOrElse(paymentEntity -> {
                            log.info("Returning money to customer: " + paymentEntity.getPrice());
                            paymentEntity.setPaymentStatus(ROLLED_BACK);
                            paymentRepository.save(paymentEntity);
                        }, () -> {
                            PaymentEntity createdPayment = paymentRepository.save(PaymentEntity.builder()
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
        return ResponseEntity.ok(paymentRepository.findAll());
    }

    @RequestMapping("/health")
    public Map<String, String> health() {
        log.info("Calling health()");
        return Map.of("status", "OK");
    }
}
