package rvm.dz.dz8cqrses.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import rvm.dz.dz8cqrses.repositories.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class AppController {

    final EventRepository eventRepository;
    final ItemRepository itemRepository;
    final OrderDenormalizedRepository orderDenormalizedRepository;
    final OrderRepository orderRepository;
    final OrdersToItemsRepository ordersToItemsRepository;
    final UserRepository userRepository;
    final KafkaTemplate<String, String> kafkaTemplate;

    //user management is not ES/CQRS
    @GetMapping("/users")
    public ResponseEntity getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping("/users")
    public ResponseEntity createUsers(@RequestBody CreateUserRequest request) {
        UserEntity createdUser = userRepository.save(
                UserEntity.builder()
                        .name(request.getName())
                        .role(request.getRole()).build()
        );

        return ResponseEntity.ok( Map.of("id", createdUser.getId()));
    }

    @GetMapping("/orders")
    public ResponseEntity getAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @SneakyThrows
    @PostMapping("/orders")
    public ResponseEntity createOrder(@RequestBody CreateOrderRequest request) {
        ObjectMapper objectMapper = new ObjectMapper();

        if (userRepository.existsById(request.getUserId())) {
            //save to DB
            OrderEntity createdOrder = orderRepository.save(OrderEntity.builder()
                    .userId(request.getUserId())
                    .createdAt(Instant.now())
                    .status(OrderEntity.Status.NEW)
                    .build());

            //publish event
            Map<String, String> eventDataMap = Map.of(
                    "userId",
                    request.getUserId().toString());

            EventEntity event = EventEntity.builder()
                .entityType(EventEntity.EntityType.ORDER)
                .entityId(createdOrder.getId())
                .eventType(EventEntity.EventType.ORDER_CREATED)
                .eventData(objectMapper.writeValueAsString(eventDataMap)).build();

//            this.kafkaTemplate.send("orderCreated", objectMapper.writeValueAsString(event));

            // save event to DB
//            String generatedOrderId = UUID.randomUUID().toString();
//            Map<String, String> eventDataMap = Map.of(
//                    "userId",
//                    request.getUserId().toString());
//            eventRepository.save(
//                    EventEntity.builder()
//                            .entityType(EventEntity.EntityType.ORDER)
//                            .entityId(generatedOrderId)
//                            .eventType(EventEntity.EventType.ORDER_CREATED)
//                            .eventData(objectMapper.writeValueAsString(eventDataMap)).build()
//            );
            // publish event
            return ResponseEntity.ok( Map.of("id", createdOrder.getId()));
        } else {
           return ResponseEntity.badRequest().body("not valid userId!");
        }
//        OrderEntity createdOrder = orderRepository.save(
//                OrderEntity.builder()
//                        .userId(request.getUserId())
//                        .status(OrderEntity.Status.NEW).build()
//        );
//
//        return ResponseEntity.ok( Map.of("id", createdOrder.getId()));
//        return ResponseEntity.ok(null);
    }

    @RequestMapping("/health")
    public Map<String, String> health() {
        log.info("Calling health()");
        Map<String, String> response = new HashMap();
        response.put("status", "OK");
        return response;
    }

    public AppController(EventRepository eventRepository, ItemRepository itemRepository,
                         OrderDenormalizedRepository orderDenormalizedRepository, OrderRepository orderRepository,
                         OrdersToItemsRepository ordersToItemsRepository, UserRepository userRepository,
                         KafkaTemplate kafkaTemplate) {
        this.eventRepository = eventRepository;
        this.itemRepository = itemRepository;
        this.orderDenormalizedRepository = orderDenormalizedRepository;
        this.orderRepository = orderRepository;
        this.ordersToItemsRepository = ordersToItemsRepository;
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;

        itemRepository.save(ItemEntity.builder().name("Table").price(2500L).build());
        itemRepository.save(ItemEntity.builder().name("Chair").price(300L).build());
        itemRepository.save(ItemEntity.builder().name("Sofa").price(700L).build());
    }
}
