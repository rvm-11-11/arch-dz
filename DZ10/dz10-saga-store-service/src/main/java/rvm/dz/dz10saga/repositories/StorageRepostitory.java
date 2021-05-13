package rvm.dz.dz10saga.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StorageRepostitory extends JpaRepository<StorageEntity, Long> {

    Optional<StorageEntity> findByOrderId(Long orderId);

}