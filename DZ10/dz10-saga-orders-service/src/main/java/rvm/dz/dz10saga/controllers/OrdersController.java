package rvm.dz.dz10saga.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import rvm.dz.dz10saga.repositories.Event;
import rvm.dz.dz10saga.repositories.OrderEntity;
import rvm.dz.dz10saga.repositories.OrderRepository;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Long.parseLong;

@RestController
@Slf4j
@RequiredArgsConstructor
public class OrdersController {

    private final OrderRepository orderRepository;

    @Autowired
    private KafkaTemplate<String, String> template;

    ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "shopping-events")
    public void listen(ConsumerRecord<?, ?> cr) throws Exception {
        log.info(cr.toString());
        Event event = new ObjectMapper().readValue((String)cr.value(), Event.class);
        applyEvent(event);
    }

    @SneakyThrows
    private void applyEvent(Event incomingEvent) {
        switch (incomingEvent.getEventType()) {
            case PAYMENT_APPROVED:
                OrderEntity orderEntity = orderRepository.findById(incomingEvent.getOrderId()).get();
                orderEntity.setPaymentStatus(OrderEntity.Status.APPROVED);
                OrderEntity savedEntity = orderRepository.save(orderEntity);
                checkIfAllPartsApproved(savedEntity);
              break;
            case PAYMENT_REJECTED:
                orderEntity = orderRepository.findById(incomingEvent.getOrderId()).get();
                orderEntity.setPaymentStatus(OrderEntity.Status.REJECTED);
                orderEntity.setOverallStatus(OrderEntity.Status.REJECTED);
                savedEntity = orderRepository.save(orderEntity);

                Event event = Event.builder()
                        .orderId(savedEntity.getOrderId())
                        .eventType(Event.EventType.ORDER_REJECTED)
                        .build();

                this.template.send( "shopping-events", objectMapper.writeValueAsString(event));

                break;
            default:
                log.info("event ignored " + incomingEvent);
        }
    }

    private void checkIfAllPartsApproved(OrderEntity order) {
        if(order.getDeliveryStatus() == OrderEntity.Status.APPROVED
                && order.getPaymentStatus() == OrderEntity.Status.APPROVED
                && order.getStoreStatus() == OrderEntity.Status.APPROVED) {
            order.setOverallStatus(OrderEntity.Status.APPROVED);
            orderRepository.save(order);
            log.info("Order executed successfully!");
        }

    }

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
