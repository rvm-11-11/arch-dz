package rvm.dz.archfinalproject.repositories;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @JsonProperty
    private Long orderId;

    @JsonProperty
    private EventType eventType;

    @JsonProperty
    private Map<String, String> eventData;

    public enum EventType {
        ORDER_PENDING, ORDER_REJECTED, ORDER_APPROVED,
        PAYMENT_REJECTED, PAYMENT_APPROVED,
        FLIGHT_BOOKING_REJECTED, FLIGHT_BOOKING_APPROVED,
        HOTEL_BOOKING_REJECTED, HOTEL_BOOKING_APPROVED
    }


}
