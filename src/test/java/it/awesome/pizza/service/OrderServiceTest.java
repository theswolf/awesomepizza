package it.awesome.pizza.service;
// src/test/java/it/awesome/pizza/service/OrderServiceTest.java

import it.awesome.pizza.model.Order;
import it.awesome.pizza.model.OrderStatus;
import it.awesome.pizza.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private SseService sseService;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder() {
        Order order = new Order();
        order.setId(1L);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order createdOrder = orderService.createOrder(order);

        assertNotNull(createdOrder);
        assertEquals(OrderStatus.PENDING, createdOrder.getStatus());
        verify(orderRepository, times(1)).save(order);
        verify(sseService, times(1)).notifyClient(anyLong(), eq(order));
    }

    @Test
    void testTakeNextOrder() {
        Order pendingOrder = new Order();
        pendingOrder.setId(1L);
        pendingOrder.setStatus(OrderStatus.PENDING);
        when(orderRepository.findByStatus(OrderStatus.PENDING)).thenReturn(List.of(pendingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(pendingOrder);

        Optional<Order> nextOrder = orderService.takeNextOrder();

        assertTrue(nextOrder.isPresent());
        assertEquals(OrderStatus.IN_PROGRESS, nextOrder.get().getStatus());
        verify(orderRepository, times(1)).findByStatus(OrderStatus.PENDING);
        verify(orderRepository, times(1)).save(pendingOrder);
        verify(sseService, times(1)).notifyClient(anyLong(), any(Order.class));
    }

    @Test
    void testCompleteOrder() {
        Order inProgressOrder = new Order();
        inProgressOrder.setId(1L);
        inProgressOrder.setStatus(OrderStatus.IN_PROGRESS);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(inProgressOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(inProgressOrder);

        Optional<Order> completedOrder = orderService.completeOrder(1L);

        assertTrue(completedOrder.isPresent());
        assertEquals(OrderStatus.READY, completedOrder.get().getStatus());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(inProgressOrder);
        verify(sseService, times(1)).notifyClient(anyLong(), eq(inProgressOrder));
    }

    @Test
    void testGetAllOrders() {
        List<Order> orders = List.of(new Order(), new Order());
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> allOrders = orderService.getAllOrders();

        assertEquals(2, allOrders.size());
        verify(orderRepository, times(1)).findAll();
    }
}
