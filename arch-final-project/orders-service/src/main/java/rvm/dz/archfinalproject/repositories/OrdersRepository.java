package rvm.dz.archfinalproject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrdersRepository extends JpaRepository<OrderEntity, Long> {

    Optional<OrderEntity> findByUserIdAndTourId(Long userId, Long tourId);

}