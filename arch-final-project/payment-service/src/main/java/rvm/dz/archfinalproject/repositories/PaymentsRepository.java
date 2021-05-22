package rvm.dz.archfinalproject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentsRepository extends JpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByOrderId(Long orderId);

}