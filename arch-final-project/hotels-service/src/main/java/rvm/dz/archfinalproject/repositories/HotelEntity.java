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
public class HotelEntity {

  @Id
  @JsonProperty
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long hotelBookingId;

  @Column(unique = true)
  @JsonProperty
  private Long orderId;

  @JsonProperty
  private Long hotelId;

  @JsonProperty
  private Status hotelBookingStatus;

  public enum Status {
    PENDING, APPROVED, REJECTED, ROLLED_BACK
  }

}

