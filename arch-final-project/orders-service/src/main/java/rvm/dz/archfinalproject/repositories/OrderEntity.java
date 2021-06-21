package rvm.dz.archfinalproject.repositories;

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
public class OrderEntity {

  @Id
  @JsonProperty
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long orderId;

  @JsonProperty
  private Long tourId;

  @JsonProperty
  private String userId;

  @JsonProperty
  private Status paymentStatus;

  @JsonProperty
  private Status flightBookingStatus;

  @JsonProperty
  private Status hotelBookingStatus;

  @JsonProperty
  private Status overallStatus;

  public enum Status {
    PENDING, APPROVED, REJECTED
  }

}

