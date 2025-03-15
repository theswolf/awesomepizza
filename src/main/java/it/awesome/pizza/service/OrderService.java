package it.awesome.pizza.service;


import it.awesome.pizza.controller.SseController;
import it.awesome.pizza.model.Order;
import it.awesome.pizza.model.OrderStatus;
import it.awesome.pizza.repository.OrderRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;




@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final SseService sseService;

    public OrderService(OrderRepository orderRepository, SseService sseService) {
        this.orderRepository = orderRepository;
        this.sseService = sseService;
    }

    public Order createOrder(Order order) {
        order.setStatus(OrderStatus.PENDING);
        Order savedOrder = orderRepository.save(order);
        notifyClient(savedOrder);
        return savedOrder;
    }

    public Optional<Order> takeNextOrder() {
        Optional<Order> nextOrderOpt = orderRepository.findByStatus(OrderStatus.PENDING).stream().findFirst();
        nextOrderOpt.ifPresent(order -> {
            // Disattiviamo eventuali ordini attualmente in preparazione
            orderRepository.findByStatus(OrderStatus.IN_PROGRESS).forEach(o -> {
                o.setActive(false);
                orderRepository.save(o);
                notifyClient(o);
            });

            order.setStatus(OrderStatus.IN_PROGRESS);
            order.setActive(true);
            orderRepository.save(order);
            notifyClient(order);
        });

        return nextOrderOpt;
    }

    public Optional<Order> completeOrder(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            if (order.getStatus() == OrderStatus.IN_PROGRESS) {
                order.setStatus(OrderStatus.READY);
                order.setActive(false);
                orderRepository.save(order);
                notifyClient(order);
            }
        }
        return orderOpt;
    }

    private void notifyClient(Order order) {
        sseService.notifyClient(order.getId(), order);
    }


    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }


}
