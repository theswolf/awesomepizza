package it.awesome.pizza.repository;

import it.awesome.pizza.model.Order;
import it.awesome.pizza.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);
}
