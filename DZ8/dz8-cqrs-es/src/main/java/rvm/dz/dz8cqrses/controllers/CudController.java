package rvm.dz.dz8cqrses.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
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
public class CudController {

    final EventRepository eventRepository;
    final ItemRepository itemRepository;
    final OrdersSearchIndex ordersSearchIndex;
    final OrderRepository orderRepository;
    final OrdersToItemsRepository ordersToItemsRepository;
    final UserRepository userRepository;
    final KafkaTemplate<String, String> kafkaTemplate;
    final ObjectMapper objectMapper = new ObjectMapper();

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

    @GetMapping("/items")
    public ResponseEntity getAllItems() {
        return ResponseEntity.ok(itemRepository.findAll());
    }

    @GetMapping("/ordersToItems")
    public ResponseEntity getAllOrdersToItems() {
        return ResponseEntity.ok(ordersToItemsRepository.findAll());
    }

    @SneakyThrows
    @PostMapping("/orders")
    public ResponseEntity createOrder(@RequestBody CreateOrderRequest request) {
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
                    request.getUserId().toString(),
                    "createdAt",
                    ((Long) createdOrder.getCreatedAt().getEpochSecond()).toString()
            );
            EventModel event = EventModel.builder()
                .entityType(EventModel.EntityType.ORDER)
                .entityId(createdOrder.getId())
                .eventType(EventModel.EventType.ORDER_CREATED)
                .eventData(eventDataMap).build();

            this.kafkaTemplate.send("shopping-events", objectMapper.writeValueAsString(event));
            return ResponseEntity.ok( Map.of("id", createdOrder.getId()));
        } else {
           return ResponseEntity.badRequest().body("not valid userId!");
        }
    }

    @SneakyThrows
    @PostMapping("/orders/{orderId}/cancel")
    public ResponseEntity cancelOrder(@PathVariable Long orderId) {
        orderRepository.findById(orderId).ifPresent(orderEntity -> {
            orderEntity.setStatus(OrderEntity.Status.CANCELLED);
            orderRepository.save(orderEntity);

            EventModel event = EventModel.builder()
                    .entityType(EventModel.EntityType.ORDER)
                    .entityId(orderId)
                    .eventType(EventModel.EventType.ORDER_CANCELLED).build();

            try {
                this.kafkaTemplate.send("shopping-events", objectMapper.writeValueAsString(event));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });

        return ResponseEntity.ok().build();
    }

    @SneakyThrows
    @PostMapping("/orders/{orderId}/confirm")
    public ResponseEntity confirmOrder(@PathVariable Long orderId) {
        orderRepository.findById(orderId).ifPresent(orderEntity -> {
            orderEntity.setStatus(OrderEntity.Status.READY_FOR_PAYMENT);
            orderRepository.save(orderEntity);

            EventModel event = EventModel.builder()
                    .entityType(EventModel.EntityType.ORDER)
                    .entityId(orderId)
                    .eventType(EventModel.EventType.ORDER_SENT_TO_PAYMENT).build();

            try {
                this.kafkaTemplate.send("shopping-events", objectMapper.writeValueAsString(event));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });

        return ResponseEntity.ok().build();
    }

    @PostMapping("/orders/{orderId}/addItem")
    public ResponseEntity addItemToOrder(@PathVariable Long orderId, @RequestBody AddItemToOrderRequest request) {
        orderRepository.findById(orderId).ifPresent(orderEntity -> {
            if (orderEntity.getStatus() == OrderEntity.Status.NEW) {
                ordersToItemsRepository.save(OrdersToItemsEntity.builder()
                        .itemId(request.getItemId())
                        .orderId(orderId)
                        .quantity(request.getQuantity()).build());

                EventModel event = EventModel.builder()
                        .entityType(EventModel.EntityType.ORDER)
                        .entityId(orderId)
                        .eventType(EventModel.EventType.ITEM_ADDED_TO_ORDER)
                        .eventData(Map.of(
                                "itemId", request.getItemId().toString(),
                                "itemName", itemRepository.findById(request.getItemId()).get().getName(),
                                "quantity", request.getQuantity().toString(),
                                "price", itemRepository.findById(request.getItemId()).get().getPrice().toString()
                        )).build();
                try {
                    this.kafkaTemplate.send("shopping-events", objectMapper.writeValueAsString(event));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

            }
        });

        return ResponseEntity.ok().build();
    }

    @PostMapping("/orders/{orderId}/removeItem")
    public ResponseEntity removeItemFromOrder(@PathVariable Long orderId, @RequestBody RemoveItemFromOrderRequest request) {
        orderRepository.findById(orderId).ifPresent(orderEntity -> {
            if (orderEntity.getStatus() == OrderEntity.Status.NEW) {
                OrdersToItemsEntity ordersToItemsEntityToBeRemoved =
                        ordersToItemsRepository.findByOrderIdAndItemId(orderId, request.getItemId()).get(0);
                ordersToItemsRepository.deleteById(ordersToItemsEntityToBeRemoved.getId());

                EventModel event = EventModel.builder()
                        .entityType(EventModel.EntityType.ORDER)
                        .entityId(orderId)
                        .eventType(EventModel.EventType.ITEM_REMOVED_FROM_ORDER)
                        .eventData(Map.of(
                                "itemId", request.getItemId().toString()
                        )).build();
                try {
                    this.kafkaTemplate.send("shopping-events", objectMapper.writeValueAsString(event));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

            }
        });

        return ResponseEntity.ok().build();
    }


    @RequestMapping("/health")
    public Map<String, String> health() {
        log.info("Calling health()");
        Map<String, String> response = new HashMap();
        response.put("status", "OK");
        return response;
    }

    public CudController(EventRepository eventRepository, ItemRepository itemRepository,
                         OrdersSearchIndex ordersSearchIndex, OrderRepository orderRepository,
                         OrdersToItemsRepository ordersToItemsRepository, UserRepository userRepository,
                         KafkaTemplate kafkaTemplate) {
        this.eventRepository = eventRepository;
        this.itemRepository = itemRepository;
        this.ordersSearchIndex = ordersSearchIndex;
        this.orderRepository = orderRepository;
        this.ordersToItemsRepository = ordersToItemsRepository;
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;

        itemRepository.save(ItemEntity.builder().name("Table").price(2500L).build());
        itemRepository.save(ItemEntity.builder().name("Chair").price(300L).build());
        itemRepository.save(ItemEntity.builder().name("Sofa").price(700L).build());
    }
}
