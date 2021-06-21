package rvm.dz.archfinalproject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HotelsRepository extends JpaRepository<HotelEntity, Long> {

    Optional<HotelEntity> findByOrderId(Long orderId);

}