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
import rvm.dz.archfinalproject.repositories.HotelEntity;
import rvm.dz.archfinalproject.repositories.HotelsRepository;

import java.util.Map;

import static java.lang.Long.parseLong;
import static rvm.dz.archfinalproject.repositories.HotelEntity.Status.ROLLED_BACK;

@RestController
@Slf4j
@RequiredArgsConstructor
public class HotelsController {

    private final HotelsRepository hotelsRepository;
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
                if(hotelsRepository.findByOrderId(incomingEvent.getOrderId()).isEmpty()) {
                    if (parseLong(incomingEvent.getEventData().get("hotelId")) > 3) {
                        HotelEntity createdBooking = hotelsRepository.save(HotelEntity.builder()
                                .orderId(incomingEvent.getOrderId())
                                .hotelId(parseLong(incomingEvent.getEventData().get("hotelId")))
                                .hotelBookingStatus(HotelEntity.Status.APPROVED)
                                .build());

                        Event outgoingEvent = Event.builder()
                                .orderId(createdBooking.getOrderId())
                                .eventType(Event.EventType.HOTEL_BOOKING_APPROVED)
                                .build();

                        this.template.send("travel-agency-events", objectMapper.writeValueAsString(outgoingEvent));
                    } else {
                        HotelEntity createdPayment = hotelsRepository.save(HotelEntity.builder()
                                .orderId(incomingEvent.getOrderId())
                                .hotelId(parseLong(incomingEvent.getEventData().get("hotelId")))
                                .hotelBookingStatus(HotelEntity.Status.REJECTED)
                                .build());

                        Event outgoingEvent = Event.builder()
                                .orderId(createdPayment.getOrderId())
                                .eventType(Event.EventType.HOTEL_BOOKING_REJECTED)
                                .build();

                        this.template.send("travel-agency-events", objectMapper.writeValueAsString(outgoingEvent));
                    }
                }
                break;
            case ORDER_REJECTED:
                hotelsRepository.findByOrderId(incomingEvent.getOrderId()).ifPresentOrElse(hotelEntity -> {
                            log.info("Unbooking hotel: id " + hotelEntity.getHotelId());
                            hotelEntity.setHotelBookingStatus(ROLLED_BACK);
                            hotelsRepository.save(hotelEntity);
                        }, () -> {
                            HotelEntity createdFlight = hotelsRepository.save(HotelEntity.builder()
                                    .orderId(incomingEvent.getOrderId())
                                    .hotelId(parseLong(incomingEvent.getEventData().get("hotelId")))
                                    .hotelBookingStatus(HotelEntity.Status.REJECTED)
                                    .build());
                        }
                );
                break;
            default:
                log.info("event ignored " + incomingEvent);
        }
    }

    @GetMapping("/hotelsBookings")
    public ResponseEntity getAllFlights() {
        return ResponseEntity.ok(hotelsRepository.findAll());
    }

    @RequestMapping("/health")
    public Map<String, String> health() {
        log.info("Calling health()");
        return Map.of("status", "OK");
    }
}
