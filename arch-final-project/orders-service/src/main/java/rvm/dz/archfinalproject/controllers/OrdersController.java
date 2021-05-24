package rvm.dz.archfinalproject.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import rvm.dz.archfinalproject.repositories.Event;
import rvm.dz.archfinalproject.repositories.OrderEntity;
import rvm.dz.archfinalproject.repositories.OrdersRepository;
import rvm.dz.archfinalproject.repositories.TourResponse;

import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
public class OrdersController {

    private final KafkaTemplate<String, String> template;
    private final OrdersRepository ordersRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    private final String USER_ID_HEADER = "X-Auth-Request-User";

    @Value("${rvm.tours-service.root-url}")
    private String toursServiceWebRootUrl;

    @GetMapping("/orders")
    public ResponseEntity getAllOrders() {
        return ResponseEntity.ok(ordersRepository.findAll());
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(ordersRepository.findById(orderId));
    }

    @SneakyThrows
    @PostMapping("/orders")
    public ResponseEntity createOrder(@RequestHeader(USER_ID_HEADER) String userId,
            @RequestBody CreateUpdateOrderRequest request) {
        Optional<OrderEntity> sameOrder = ordersRepository
                .findByUserIdAndTourId(Long.valueOf(userId), request.getTourId());
        if (sameOrder.isPresent()) {
            return ResponseEntity.status( HttpStatus.CONFLICT).body(
                    Map.of("sameOrderExistedBefore", sameOrder.get()) );
        }

        OrderEntity createdOrder = ordersRepository.save(
                OrderEntity.builder()
                        .tourId(request.getTourId())
                        .userId(Long.valueOf(userId))
                        .paymentStatus(OrderEntity.Status.PENDING)
                        .flightBookingStatus(OrderEntity.Status.PENDING)
                        .hotelBookingStatus(OrderEntity.Status.PENDING)
                        .overallStatus(OrderEntity.Status.PENDING)
                        .build()
        );

        TourResponse tour = restTemplate
                .getForObject(toursServiceWebRootUrl + "/tours/" + request.getTourId(), TourResponse.class);


        Map<String, String> eventDataMap = Map.of(
                "orderId", createdOrder.getOrderId().toString(),
                "price", tour.getPrice().toString(),
                "fromDestination", tour.getFromDestination(),
                "fromDate", ((Long) tour.getFromDate().getEpochSecond()).toString(),
                "toDate", ((Long) tour.getToDate().getEpochSecond()).toString(),
                "toDestination", tour.getToDestination(),
                "hotelId", tour.getHotelId().toString()
        );

        Event event = Event.builder()
                .orderId(createdOrder.getOrderId())
                .eventType(Event.EventType.ORDER_PENDING)
                .eventData(eventDataMap)
                .build();

        this.template.send( "travel-agency-events", objectMapper.writeValueAsString(event));

        return ResponseEntity.ok( Map.of("orderId", createdOrder.getOrderId()) );
    }


    @KafkaListener(topics = "travel-agency-events")
    public void listen(ConsumerRecord<?, ?> cr) throws Exception {
        log.info(cr.toString());
        Event event = new ObjectMapper().readValue((String)cr.value(), Event.class);
        applyEvent(event);
    }

    @SneakyThrows
    private void applyEvent(Event incomingEvent) {
        switch (incomingEvent.getEventType()) {
            case PAYMENT_APPROVED:
                OrderEntity orderEntity = ordersRepository.findById(incomingEvent.getOrderId()).get();
                orderEntity.setPaymentStatus(OrderEntity.Status.APPROVED);
                OrderEntity savedEntity = ordersRepository.save(orderEntity);
                checkIfAllPartsApproved(savedEntity);
                break;
            case PAYMENT_REJECTED:
                orderEntity = ordersRepository.findById(incomingEvent.getOrderId()).get();
                orderEntity.setPaymentStatus(OrderEntity.Status.REJECTED);
                orderEntity.setOverallStatus(OrderEntity.Status.REJECTED);
                savedEntity = ordersRepository.save(orderEntity);

                Event event = Event.builder()
                        .orderId(savedEntity.getOrderId())
                        .eventType(Event.EventType.ORDER_REJECTED)
                        .build();

                this.template.send( "travel-agency-events", objectMapper.writeValueAsString(event));

                break;
            case FLIGHT_BOOKING_APPROVED:
                orderEntity = ordersRepository.findById(incomingEvent.getOrderId()).get();
                orderEntity.setFlightBookingStatus(OrderEntity.Status.APPROVED);
                savedEntity = ordersRepository.save(orderEntity);
                checkIfAllPartsApproved(savedEntity);
                break;
            case FLIGHT_BOOKING_REJECTED:
                orderEntity = ordersRepository.findById(incomingEvent.getOrderId()).get();
                orderEntity.setFlightBookingStatus(OrderEntity.Status.REJECTED);
                orderEntity.setOverallStatus(OrderEntity.Status.REJECTED);
                savedEntity = ordersRepository.save(orderEntity);

                event = Event.builder()
                        .orderId(savedEntity.getOrderId())
                        .eventType(Event.EventType.ORDER_REJECTED)
                        .build();

                this.template.send( "travel-agency-events", objectMapper.writeValueAsString(event));
            case HOTEL_BOOKING_APPROVED:
                orderEntity = ordersRepository.findById(incomingEvent.getOrderId()).get();
                orderEntity.setHotelBookingStatus(OrderEntity.Status.APPROVED);
                savedEntity = ordersRepository.save(orderEntity);
                checkIfAllPartsApproved(savedEntity);
                break;
            case HOTEL_BOOKING_REJECTED:
                orderEntity = ordersRepository.findById(incomingEvent.getOrderId()).get();
                orderEntity.setHotelBookingStatus(OrderEntity.Status.REJECTED);
                orderEntity.setOverallStatus(OrderEntity.Status.REJECTED);
                savedEntity = ordersRepository.save(orderEntity);

                event = Event.builder()
                        .orderId(savedEntity.getOrderId())
                        .eventType(Event.EventType.ORDER_REJECTED)
                        .build();

                this.template.send( "travel-agency-events", objectMapper.writeValueAsString(event));

                break;
            default:
                log.info("event ignored " + incomingEvent);
        }
    }

    @SneakyThrows
    private void checkIfAllPartsApproved(OrderEntity order) {
        if(order.getPaymentStatus() == OrderEntity.Status.APPROVED
                && order.getFlightBookingStatus() == OrderEntity.Status.APPROVED
                && order.getHotelBookingStatus() == OrderEntity.Status.APPROVED) {
            order.setOverallStatus(OrderEntity.Status.APPROVED);
            ordersRepository.save(order);


            Map<String, String> eventDataMap = Map.of(
                    "tourId", order.getTourId().toString(),
                    "userId", order.getUserId().toString()
            );

            Event event = Event.builder()
                    .orderId(order.getOrderId())
                    .eventType(Event.EventType.ORDER_OVERALL_APPROVED)
                    .eventData(eventDataMap)
                    .build();

            this.template.send( "travel-agency-events", objectMapper.writeValueAsString(event));

            log.info("Order executed successfully!");
        }

    }

    @PostMapping("/orders/reset")
    public String resetOrders() {
        log.info("Calling resetOrders()");
        ordersRepository.deleteAll();
        return "All orders removed";
    }

    @RequestMapping("/health")
    public Map<String, String> health() {
        log.info("Calling health()");
        return Map. of("status", "OK");
    }

}
