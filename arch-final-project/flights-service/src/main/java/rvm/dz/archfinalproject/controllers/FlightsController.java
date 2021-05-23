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
import rvm.dz.archfinalproject.repositories.FlightEntity;
import rvm.dz.archfinalproject.repositories.FlightsRepository;

import java.util.Map;

import static java.lang.Long.parseLong;
import static rvm.dz.archfinalproject.repositories.FlightEntity.Status.ROLLED_BACK;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FlightsController {

    private final FlightsRepository flightsRepository;
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
                if(flightsRepository.findByOrderId(incomingEvent.getOrderId()).isEmpty()) {
                    if (!incomingEvent.getEventData().get("toDestination").equalsIgnoreCase("Oslo")) {
                        FlightEntity createdFlight = flightsRepository.save(FlightEntity.builder()
                                .orderId(incomingEvent.getOrderId())
                                .fromDestination(incomingEvent.getEventData().get("fromDestination"))
                                .toDestination(incomingEvent.getEventData().get("toDestination"))
                                .flightStatus(FlightEntity.Status.APPROVED)
                                .build());

                        Event outgoingEvent = Event.builder()
                                .orderId(createdFlight.getOrderId())
                                .eventType(Event.EventType.FLIGHT_BOOKING_APPROVED)
                                .build();

                        this.template.send("travel-agency-events", objectMapper.writeValueAsString(outgoingEvent));
                    } else {
                        FlightEntity createdPayment = flightsRepository.save(FlightEntity.builder()
                                .orderId(incomingEvent.getOrderId())
                                .fromDestination(incomingEvent.getEventData().get("fromDestination"))
                                .toDestination(incomingEvent.getEventData().get("toDestination"))
                                .flightStatus(FlightEntity.Status.REJECTED)
                                .build());

                        Event outgoingEvent = Event.builder()
                                .orderId(createdPayment.getOrderId())
                                .eventType(Event.EventType.FLIGHT_BOOKING_REJECTED)
                                .build();

                        this.template.send("travel-agency-events", objectMapper.writeValueAsString(outgoingEvent));
                    }
                }
                break;
            case ORDER_REJECTED:
                flightsRepository.findByOrderId(incomingEvent.getOrderId()).ifPresentOrElse(flightEntity -> {
                            log.info("Unbooking flight: from " + flightEntity.getFromDestination() +
                                    " to " + flightEntity.getToDestination());
                            flightEntity.setFlightStatus(ROLLED_BACK);
                            flightsRepository.save(flightEntity);
                        }, () -> {
                            FlightEntity createdFlight = flightsRepository.save(FlightEntity.builder()
                                    .orderId(incomingEvent.getOrderId())
                                    .fromDestination(incomingEvent.getEventData().get("fromDestination"))
                                    .toDestination(incomingEvent.getEventData().get("toDestination"))
                                    .flightStatus(FlightEntity.Status.REJECTED)
                                    .build());
                        }
                );
                break;
            default:
                log.info("event ignored " + incomingEvent);
        }
    }

    @GetMapping("/flights")
    public ResponseEntity getAllFlights() {
        return ResponseEntity.ok(flightsRepository.findAll());
    }

    @PostMapping("/resetFlights")
    public String resetFlights() {
        log.info("Calling resetFlights()");
        flightsRepository.deleteAll();
        return "All Flights removed";
    }

    @RequestMapping("/health")
    public Map<String, String> health() {
        log.info("Calling health()");
        return Map.of("status", "OK");
    }
}
