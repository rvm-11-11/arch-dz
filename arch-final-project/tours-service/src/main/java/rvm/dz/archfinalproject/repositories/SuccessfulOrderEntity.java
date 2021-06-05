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
public class SuccessfulOrderEntity {

    @Id
    @JsonProperty
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long successfulOrderId;

    @JsonProperty
    private Long tourId;

    @JsonProperty
    private String tourName;

    @JsonProperty
    private String tourDescription;

    @JsonProperty
    private Long price;

    @JsonProperty
    private Instant fromDate;

    @JsonProperty
    private Instant toDate;

    @JsonProperty
    private String fromDestination;

    @JsonProperty
    private String toDestination;

    @JsonProperty
    private Long hotelId;

    @JsonProperty
    private String userId;

//    @JsonProperty
//    private String userName;

    @JsonProperty
    private Status overallOrderStatus;

    public enum Status {
        PENDING, APPROVED, REJECTED
    }

}
