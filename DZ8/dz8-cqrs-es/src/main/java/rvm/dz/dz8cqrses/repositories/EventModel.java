package rvm.dz.dz8cqrses.repositories;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventModel {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("entityType")
    private EntityType entityType;

    @JsonProperty("entityId")
    private Long entityId;

    @JsonProperty("eventType")
    private EventType eventType;

    @JsonProperty("eventData")
    private Map<String, String> eventData;

    public enum EventType {
        ORDER_CREATED, ORDER_CANCELLED, ORDER_SENT_TO_PAYMENT, ITEM_ADDED_TO_ORDER, ITEM_REMOVED_FROM_ORDER
    }

    public enum EntityType {
        ORDER, ITEM
    }

}
