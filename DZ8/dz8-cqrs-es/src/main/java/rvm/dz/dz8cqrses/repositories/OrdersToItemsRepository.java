package rvm.dz.dz8cqrses.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersToItemsRepository extends JpaRepository<OrdersToItemsEntity, Long> {
    List<OrdersToItemsEntity> findByOrderIdAndItemId(Long userId, Long itemId);
}