package rvm.dz.dz10saga.repositories;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

//  @JsonProperty
//  private EntityType entityType;

  @JsonProperty
  private Long orderId;

  @JsonProperty
  private EventType eventType;

  @JsonProperty
  private Map<String, String> eventData;

  public enum EventType {
    ORDER_PENDING, ORDER_REJECTED, ORDER_APPROVED,
    PAYMENT_REJECTED, PAYMENT_APPROVED,
    DELIVERY_REJECTED, DELIVERY_APPROVED,
    STORE_RESERVATION_REJECTED, STORE_RESERVATION_APPROVED
  }

}

