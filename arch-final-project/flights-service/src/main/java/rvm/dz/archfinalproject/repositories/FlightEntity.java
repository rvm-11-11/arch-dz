package rvm.dz.archfinalproject.repositories;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightEntity {

  @Id
  @JsonProperty
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long flightId;

  @Column(unique = true)
  @JsonProperty
  private Long orderId;

  @JsonProperty
  private String fromDestination;

  @JsonProperty
  private String toDestination;

  @JsonProperty
  private Status flightStatus;

  public enum Status {
    PENDING, APPROVED, REJECTED, ROLLED_BACK
  }

}

