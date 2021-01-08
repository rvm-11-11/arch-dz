package rvm.dz.dz6users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    Optional<NotificationEntity> findByOrderId(Long orderId);

}