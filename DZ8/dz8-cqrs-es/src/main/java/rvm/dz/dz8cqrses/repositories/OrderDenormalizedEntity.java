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
import java.time.Instant;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDenormalizedEntity {
    @Id
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("status")
    private Status status;

    @JsonProperty("createdAt")
    private Instant createdAt;

    @JsonProperty("total")
    private Long total;

    @JsonProperty("itemsList")
    private String itemsList;

    public enum Status {
        NEW, CANCELLED, READY_FOR_PAYMENT
    }

}
