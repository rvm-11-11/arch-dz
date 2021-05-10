package rvm.dz.dz8cqrses.repositories;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity {
    @Id
    @JsonProperty("id")
    private Long id;

    @JsonProperty("entityType")
    private EntityType entityType;

    @JsonProperty("entityId")
    private Long entityId;

    @JsonProperty("eventType")
    private EventType eventType;

    @JsonProperty("eventData")
    private String eventData;

    public enum EventType {
        ORDER_CREATED, ORDER_CANCELLED, ORDER_SENT_TO_PAYMENT
    }

    public enum EntityType {
        ORDER
    }

}
