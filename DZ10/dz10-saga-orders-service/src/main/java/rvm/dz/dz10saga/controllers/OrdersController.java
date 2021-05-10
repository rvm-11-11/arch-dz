package rvm.dz.dz10saga.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import rvm.dz.dz10saga.repositories.Event;
import rvm.dz.dz10saga.repositories.OrderEntity;
import rvm.dz.dz10saga.repositories.OrderRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class OrdersController {

    private final OrderRepository orderRepository;

    @Autowired
    private KafkaTemplate<String, String> template;

    ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/orders")
    public ResponseEntity order(@RequestBody OrderRequest request) throws JsonProcessingException {
        OrderEntity createdOrder = orderRepository.save(
                OrderEntity.builder()
                        .userId(request.getUserId())
                        .itemId(request.getItemId())
                        .price(request.getPrice())
                        .deliveryDate(request.getDeliveryDate().toInstant())
                        .paymentStatus(OrderEntity.Status.PENDING)
                        .deliveryStatus(OrderEntity.Status.PENDING)
                        .storeStatus(OrderEntity.Status.PENDING)
                        .overallStatus(OrderEntity.Status.PENDING)
                        .build()
        );

        Map<String, String> eventDataMap = Map.of(
                "itemId",
                createdOrder.getItemId().toString(),
                "price",
                createdOrder.getPrice().toString(),
                "deliveryDate",
                ((Long) createdOrder.getDeliveryDate().getEpochSecond()).toString()
        );

        Event event = Event.builder()
                .orderId(createdOrder.getOrderId())
                .eventType(Event.EventType.ORDER_PENDING)
                .eventData(eventDataMap)
                .build();

        this.template.send( "shopping-events", objectMapper.writeValueAsString(event));

        return ResponseEntity.ok( Map.of("id", createdOrder.getOrderId()) );
    }

    @GetMapping("/orders")
    public ResponseEntity getAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @RequestMapping("/health")
    public Map<String, String> health() {
        log.info("Calling health()");
        Map<String, String> response = new HashMap();
        response.put("status", "OK");
        return response;
    }
}
