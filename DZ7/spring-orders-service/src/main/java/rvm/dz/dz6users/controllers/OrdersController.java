package rvm.dz.dz6users.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Optional;

@RestController
@Slf4j
//@RequiredArgsConstructor
public class OrdersController {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    @Autowired
    private KafkaTemplate<String, String> template;
    ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/checkout")
    public ResponseEntity checkout(@RequestBody CheckoutRequest request) {
        Long sum = itemRepository.findById(request.getItemId()).get().getPrice();

        OrderEntity createdOrder = orderRepository.save(
                OrderEntity.builder()
                        .itemId(request.getItemId())
                        .userId(request.getUserId())
                        .sum(sum)
                        .status(OrderEntity.Status.WAITING_CONFIRMATION).build()
        );

        return ResponseEntity.ok( Map.of("id", createdOrder.getId(), "sum", sum) );
    }

    @PostMapping("/confirmOrder")
    public ResponseEntity confirmOrder(@RequestBody ConfirmationRequest request) throws JsonProcessingException {
        Optional<OrderEntity> orderOptional = orderRepository.findById(request.orderId);

        if (orderOptional.isPresent()) {
            OrderEntity order = orderOptional.get();
            if (order.getStatus() ==  OrderEntity.Status.WAITING_CONFIRMATION) {
                order.setStatus(OrderEntity.Status.CONFIRMED);
                orderRepository.save(order);

                OrderCreatedEvent event = OrderCreatedEvent.builder()
                        .orderId(order.getId())
                        .userId(order.getUserId())
                        .sum(order.getSum()).build();
//                this.template.send( "orderCreated", objectMapper.writeValueAsString(event));
                return ResponseEntity.ok("Order confirmed");
            } else {
                return ResponseEntity.badRequest().body("This order confirmed earlier, create a new one if necessary!");
            }
        } else {
            return ResponseEntity.badRequest().body("Order not found, please go to /checkout first!");
        }
    }

    @GetMapping("/orders")
    public ResponseEntity getAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @GetMapping("/items")
    public ResponseEntity getAllItems() {
        return ResponseEntity.ok(itemRepository.findAll());
    }

    public OrdersController(OrderRepository orderRepository, ItemRepository itemRepository) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        itemRepository.save(ItemEntity.builder().price(2500L).build());
        itemRepository.save(ItemEntity.builder().price(300L).build());
        itemRepository.save(ItemEntity.builder().price(700L).build());
    }

    @RequestMapping("/health")
    public Map<String, String> health() {
        log.info("Calling health()");
        Map<String, String> response = new HashMap();
        response.put("status", "OK");
        return response;
    }
}
