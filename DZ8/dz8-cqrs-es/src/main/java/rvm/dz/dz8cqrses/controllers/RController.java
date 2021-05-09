package rvm.dz.dz8cqrses.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import rvm.dz.dz8cqrses.repositories.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
public class RController {

    final OrdersSearchIndex ordersSearchIndex;
    final KafkaTemplate<String, String> kafkaTemplate;
    final static ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "shopping-events")
    public void listen(ConsumerRecord<?, ?> cr) throws Exception {
        log.info(cr.toString());
        EventModel event = new ObjectMapper().readValue((String)cr.value(), EventModel.class);
        applyEvent(event);
    }

    private void applyEvent(EventModel event) {
        switch(event.getEventType()) {
            case ORDER_CREATED:
                ordersSearchIndex.save(OrderDenormalizedEntity.builder()
                        .createdAt(Instant.ofEpochSecond(Long.parseLong(event.getEventData().get("createdAt"))))
                        .status(OrderDenormalizedEntity.Status.NEW)
                        .userId(Long.parseLong(event.getEventData().get("userId")))
                        .orderId(event.getEntityId())
                        .itemsList("[]")
                        .total(0L)
                        .build());
                break;
            case ORDER_CANCELLED:
                ordersSearchIndex.findById(event.getEntityId()).ifPresent(order -> {
                    order.setStatus(OrderDenormalizedEntity.Status.CANCELLED);
                    ordersSearchIndex.save(order);
                });
                break;
            case ORDER_SENT_TO_PAYMENT:
                ordersSearchIndex.findById(event.getEntityId()).ifPresent(order -> {
                    order.setStatus(OrderDenormalizedEntity.Status.READY_FOR_PAYMENT);
                    ordersSearchIndex.save(order);
                });
                break;
            case ITEM_ADDED_TO_ORDER:
                ordersSearchIndex.findById(event.getEntityId()).ifPresent(order -> {
                    try {
                        List<DenormalizedItem> items =
                                objectMapper.readValue(order.getItemsList(),
                                        new TypeReference<List<DenormalizedItem>>(){});

                        DenormalizedItem itemToAdd = DenormalizedItem.builder()
                                .id(Long.parseLong(event.getEventData().get("itemId")))
                                .name(event.getEventData().get("itemName"))
                                .price(Long.parseLong(event.getEventData().get("price")))
                                .quantity(Long.parseLong(event.getEventData().get("quantity")))
                                .build();

                        items.add(itemToAdd);

                        order.setItemsList(objectMapper.writeValueAsString(items));
                        order.setTotal(order.getTotal() + itemToAdd.getPrice() * itemToAdd.getQuantity());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    ordersSearchIndex.save(order);
                });
                break;
            case ITEM_REMOVED_FROM_ORDER:
                ordersSearchIndex.findById(event.getEntityId()).ifPresent(order -> {
                    try {
                        List<DenormalizedItem> items =
                                objectMapper.readValue(order.getItemsList(),
                                        new TypeReference<List<DenormalizedItem>>(){});

                        DenormalizedItem itemToBeRemoved = items.stream()
                                .filter(item -> !item.getId().equals(event.getEventData().get("itemId")))
                                .findFirst().get();
                        items.remove(itemToBeRemoved);

                        order.setItemsList(objectMapper.writeValueAsString(items));
                        order.setTotal(order.getTotal() - itemToBeRemoved.getPrice() * itemToBeRemoved.getQuantity());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    ordersSearchIndex.save(order);
                });
                break;
            default:
                log.error("Unknown event type!");
        }
    }

    @GetMapping("/ordersSearch")
    public ResponseEntity ordersSearch(@RequestParam(required = false) String orderField,
                                       @RequestParam(required = false) String statusFiltering) {
        return ResponseEntity.ok(
                ordersSearchIndex.findAll().stream().filter(order -> {
                    if (statusFiltering == null) {
                        return true;
                    } else {
                        return order.getStatus().name().equals(statusFiltering);
                    }
                }).map(RController::entityToOutput).collect(Collectors.toList())
        );
    }

    @SneakyThrows
    private static OrderOutput entityToOutput(OrderDenormalizedEntity orderDenormalizedEntity) {
        return OrderOutput.builder()
                .createdAt(orderDenormalizedEntity.getCreatedAt())
                .itemsList(objectMapper.readValue(orderDenormalizedEntity.getItemsList(), new TypeReference<List<DenormalizedItem>>(){}))
                .orderId(orderDenormalizedEntity.getOrderId())
                .status(OrderOutput.Status.valueOf(orderDenormalizedEntity.getStatus().toString()))
                .total(orderDenormalizedEntity.getTotal())
                .userId(orderDenormalizedEntity.getUserId())
                .build();
    }

    @GetMapping("/ordersSearch/{orderId}")
    public ResponseEntity ordersSearchById(@PathVariable Long orderId) {
        return ResponseEntity.ok(entityToOutput(ordersSearchIndex.findById(orderId).get()));
    }

}
