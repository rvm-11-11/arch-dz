package rvm.dz.archfinalproject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FlightsRepository extends JpaRepository<FlightEntity, Long> {

    Optional<FlightEntity> findByOrderId(Long orderId);

}