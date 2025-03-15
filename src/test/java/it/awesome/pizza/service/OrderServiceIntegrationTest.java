package it.awesome.pizza.service;

import it.awesome.pizza.model.Order;
import it.awesome.pizza.model.OrderStatus;
import it.awesome.pizza.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SseService sseService;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    void testCreateOrder() {
        Order order = new Order();
        //order.setId(1L);

        Order createdOrder = orderService.createOrder(order);

        assertNotNull(createdOrder);
        assertEquals(OrderStatus.PENDING, createdOrder.getStatus());
        assertTrue(orderRepository.findById(createdOrder.getId()).isPresent());
    }

    @Test
    void testTakeNextOrder() {
        Order pendingOrder = new Order();
        pendingOrder.setStatus(OrderStatus.PENDING);
        orderRepository.save(pendingOrder);

        Optional<Order> nextOrder = orderService.takeNextOrder();

        assertTrue(nextOrder.isPresent());
        assertEquals(OrderStatus.IN_PROGRESS, nextOrder.get().getStatus());
    }

    @Test
    void testCompleteOrder() {
        Order inProgressOrder = new Order();
        inProgressOrder.setStatus(OrderStatus.IN_PROGRESS);
        orderRepository.save(inProgressOrder);

        Optional<Order> completedOrder = orderService.completeOrder(inProgressOrder.getId());

        assertTrue(completedOrder.isPresent());
        assertEquals(OrderStatus.READY, completedOrder.get().getStatus());
    }

    @Test
    void testGetAllOrders() {
        Order order1 = new Order();
        Order order2 = new Order();
        orderRepository.save(order1);
        orderRepository.save(order2);

        List<Order> allOrders = orderService.getAllOrders();

        assertEquals(2, allOrders.size());
    }
}