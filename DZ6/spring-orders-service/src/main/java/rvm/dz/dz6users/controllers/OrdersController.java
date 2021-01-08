package rvm.dz.dz6users.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import rvm.dz.dz6users.repositories.ItemEntity;
import rvm.dz.dz6users.repositories.ItemRepository;
import rvm.dz.dz6users.repositories.OrderEntity;
import rvm.dz.dz6users.repositories.OrderRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
//@RequiredArgsConstructor
public class OrdersController {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;

    @Autowired
    private KafkaTemplate<String, String> template;

    ObjectMapper objectMapper = new ObjectMapper();

    public OrdersController(OrderRepository orderRepository, ItemRepository itemRepository) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        itemRepository.save(ItemEntity.builder().price(2500L).build());
        itemRepository.save(ItemEntity.builder().price(300L).build());
        itemRepository.save(ItemEntity.builder().price(700L).build());
    }

    @PostMapping("/order")
    public ResponseEntity order(@RequestBody OrderRequest request) throws JsonProcessingException {
        OrderEntity createdOrder = orderRepository.save(
                OrderEntity.builder()
                        .itemId(request.getItemId())
                        .userId(request.getUserId()).build()
        );

        Long sum = itemRepository.findById(createdOrder.getItemId()).get().getPrice();
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(createdOrder.getId())
                .userId(createdOrder.getUserId())
                .sum(sum).build();

        this.template.send( "orderCreated", objectMapper.writeValueAsString(event));

        return ResponseEntity.ok( Map.of("id", createdOrder.getId()) );
    }

    @GetMapping("/orders")
    public ResponseEntity getAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @GetMapping("/items")
    public ResponseEntity getAllItems() {
        return ResponseEntity.ok(itemRepository.findAll());
    }

    @RequestMapping("/health")
    public Map<String, String> health() {
        log.info("Calling health()");
        Map<String, String> response = new HashMap();
        response.put("status", "OK");
        return response;
    }
}
