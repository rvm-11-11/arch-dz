package rvm.dz.archfinalproject.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rvm.dz.archfinalproject.repositories.TourEntity;
import rvm.dz.archfinalproject.repositories.ToursRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Long.parseLong;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ToursController {

    private final ToursRepository toursRepository;

    @GetMapping("/toursSearchFilter") //TODO can be CQRS
    public ResponseEntity toursSearch(@RequestParam(required = false) String orderField,
                                       @RequestParam(required = false) String fromDestination) {
        List<TourEntity> filteredList = toursRepository.findAll().stream().filter(tour -> {
            if (fromDestination == null) {
                return true;
            } else {
                return tour.getFromDestination().equalsIgnoreCase(fromDestination);
            }
        }).collect(Collectors.toList());

        if(orderField != null && orderField.equals("price")) {
            filteredList.sort(Comparator.comparing(TourEntity::getPrice));
        }

        return ResponseEntity.ok(filteredList);
    }


    @GetMapping("/tours")
    public ResponseEntity getAllTours() {
        return ResponseEntity.ok(toursRepository.findAll());
    }

    @GetMapping("/tours/{tourId}")
    public ResponseEntity getTourById(@PathVariable Long tourId) {
        return ResponseEntity.ok(toursRepository.findById(tourId));
    }

    @PostMapping("/tours")
    public ResponseEntity createTour(@RequestBody CreateUpdateTourRequest request) {
        TourEntity createdTour = toursRepository.save(
                TourEntity.builder()
                        .name(request.getName())
                        .description(request.getDescription())
                        .price(request.getPrice())
                        .fromDate(request.getFromDate().toInstant())
                        .toDate(request.getToDate().toInstant())
                        .fromDestination(request.getFromDestination())
                        .toDestination(request.getToDestination())
                        .hotelId(request.getHotelId())
                        .build()
        );

        return ResponseEntity.ok( Map.of("tourId", createdTour.getTourId()) );
    }

    @PutMapping("/tours/{tourId}")
    public ResponseEntity updateTour(@PathVariable Long tourId, @RequestBody CreateUpdateTourRequest request) {
        TourEntity updatedTour = toursRepository.save(
                TourEntity.builder()
                        .tourId(tourId)
                        .name(request.getName())
                        .description(request.getDescription())
                        .price(request.getPrice())
                        .fromDate(request.getFromDate().toInstant())
                        .toDate(request.getToDate().toInstant())
                        .fromDestination(request.getFromDestination())
                        .toDestination(request.getToDestination())
                        .hotelId(request.getHotelId())
                        .build()
        );

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/tours/{tourId}")
    public ResponseEntity deleteTourById(@PathVariable Long tourId) {
        toursRepository.deleteById(tourId);

        return ResponseEntity.ok().build();
    }

    @RequestMapping("/health")
    public Map<String, String> health() {
        log.info("Calling health()");
        return Map.of("status", "OK");
    }

//    @Autowired
//    private KafkaTemplate<String, String> template;


//    @KafkaListener(topics = "shopping-events")
//    public void listen(ConsumerRecord<?, ?> cr) throws Exception {
//        log.info(cr.toString());
//        Event event = new ObjectMapper().readValue((String)cr.value(), Event.class);
//        applyEvent(event);
//    }

//    @SneakyThrows
//    private void applyEvent(Event incomingEvent) {
//        switch (incomingEvent.getEventType()) {
//            case PAYMENT_APPROVED:
//                OrderEntity orderEntity = productsRepository.findById(incomingEvent.getOrderId()).get();
//                orderEntity.setPaymentStatus(OrderEntity.Status.APPROVED);
//                OrderEntity savedEntity = productsRepository.save(orderEntity);
//                checkIfAllPartsApproved(savedEntity);
//              break;
//            case PAYMENT_REJECTED:
//                orderEntity = productsRepository.findById(incomingEvent.getOrderId()).get();
//                orderEntity.setPaymentStatus(OrderEntity.Status.REJECTED);
//                orderEntity.setOverallStatus(OrderEntity.Status.REJECTED);
//                savedEntity = productsRepository.save(orderEntity);
//
//                Event event = Event.builder()
//                        .orderId(savedEntity.getOrderId())
//                        .eventType(Event.EventType.ORDER_REJECTED)
//                        .build();
//
//                this.template.send( "shopping-events", objectMapper.writeValueAsString(event));
//
//                break;
//            case DELIVERY_APPROVED:
//                orderEntity = productsRepository.findById(incomingEvent.getOrderId()).get();
//                orderEntity.setDeliveryStatus(OrderEntity.Status.APPROVED);
//                savedEntity = productsRepository.save(orderEntity);
//                checkIfAllPartsApproved(savedEntity);
//                break;
//            case DELIVERY_REJECTED:
//                orderEntity = productsRepository.findById(incomingEvent.getOrderId()).get();
//                orderEntity.setDeliveryStatus(OrderEntity.Status.REJECTED);
//                orderEntity.setOverallStatus(OrderEntity.Status.REJECTED);
//                savedEntity = productsRepository.save(orderEntity);
//
//                event = Event.builder()
//                        .orderId(savedEntity.getOrderId())
//                        .eventType(Event.EventType.ORDER_REJECTED)
//                        .build();
//
//                this.template.send( "shopping-events", objectMapper.writeValueAsString(event));
//            case STORE_RESERVATION_APPROVED:
//                orderEntity = productsRepository.findById(incomingEvent.getOrderId()).get();
//                orderEntity.setStoreStatus(OrderEntity.Status.APPROVED);
//                savedEntity = productsRepository.save(orderEntity);
//                checkIfAllPartsApproved(savedEntity);
//                break;
//            case STORE_RESERVATION_REJECTED:
//                orderEntity = productsRepository.findById(incomingEvent.getOrderId()).get();
//                orderEntity.setStoreStatus(OrderEntity.Status.REJECTED);
//                orderEntity.setOverallStatus(OrderEntity.Status.REJECTED);
//                savedEntity = productsRepository.save(orderEntity);
//
//                event = Event.builder()
//                        .orderId(savedEntity.getOrderId())
//                        .eventType(Event.EventType.ORDER_REJECTED)
//                        .build();
//
//                this.template.send( "shopping-events", objectMapper.writeValueAsString(event));
//
//                break;
//            default:
//                log.info("event ignored " + incomingEvent);
//        }
//    }
//
//    private void checkIfAllPartsApproved(OrderEntity order) {
//        if(order.getDeliveryStatus() == OrderEntity.Status.APPROVED
//                && order.getPaymentStatus() == OrderEntity.Status.APPROVED
//                && order.getStoreStatus() == OrderEntity.Status.APPROVED) {
//            order.setOverallStatus(OrderEntity.Status.APPROVED);
//            productsRepository.save(order);
//            log.info("Order executed successfully!");
//        }
//
//    }
//
//    @PostMapping("/orders")
//    public ResponseEntity order(@RequestBody OrderRequest request) throws JsonProcessingException {
//        OrderEntity createdOrder = productsRepository.save(
//                OrderEntity.builder()
//                        .userId(request.getUserId())
//                        .itemId(request.getItemId())
//                        .price(request.getPrice())
//                        .deliveryDate(request.getDeliveryDate().toInstant())
//                        .paymentStatus(OrderEntity.Status.PENDING)
//                        .deliveryStatus(OrderEntity.Status.PENDING)
//                        .storeStatus(OrderEntity.Status.PENDING)
//                        .overallStatus(OrderEntity.Status.PENDING)
//                        .build()
//        );
//
//        Map<String, String> eventDataMap = Map.of(
//                "itemId",
//                createdOrder.getItemId().toString(),
//                "price",
//                createdOrder.getPrice().toString(),
//                "deliveryDate",
//                ((Long) createdOrder.getDeliveryDate().getEpochSecond()).toString()
//        );
//
//        Event event = Event.builder()
//                .orderId(createdOrder.getOrderId())
//                .eventType(Event.EventType.ORDER_PENDING)
//                .eventData(eventDataMap)
//                .build();
//
//        this.template.send( "shopping-events", objectMapper.writeValueAsString(event));
//
//        return ResponseEntity.ok( Map.of("id", createdOrder.getOrderId()) );
//    }

}
