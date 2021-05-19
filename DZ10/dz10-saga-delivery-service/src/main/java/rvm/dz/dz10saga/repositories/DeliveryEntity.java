package rvm.dz.dz10saga.repositories;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryEntity {

  @Id
  @JsonProperty
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long deliveryId;

  @Column(unique = true)
  @JsonProperty
  private Long orderId;

  @JsonProperty
  private Instant deliveryDate;

  @JsonProperty
  private Status deliveryStatus;

  public enum Status {
    PENDING, APPROVED, REJECTED, ROLLED_BACK
  }

}

