package rvm.dz.dz8cqrses.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersSearchIndex extends JpaRepository<OrderDenormalizedEntity, Long> {

}