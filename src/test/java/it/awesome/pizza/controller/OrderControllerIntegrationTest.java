package it.awesome.pizza.controller;

import it.awesome.pizza.model.Order;
import it.awesome.pizza.model.OrderStatus;
import it.awesome.pizza.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    void testCreateOrder() throws Exception {
        String orderJson = "{\"pizzaType\": \"MARGHERITA\"}";

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(OrderStatus.PENDING.name()));
    }

    @Test
    void testTakeNextOrder() throws Exception {
        Order pendingOrder = new Order();
        pendingOrder.setStatus(OrderStatus.PENDING);
        orderRepository.save(pendingOrder);

        mockMvc.perform(post("/orders/next"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(OrderStatus.IN_PROGRESS.name()));
    }

    @Test
    void testCompleteOrder() throws Exception {
        Order inProgressOrder = new Order();
        inProgressOrder.setStatus(OrderStatus.IN_PROGRESS);
        orderRepository.save(inProgressOrder);

        mockMvc.perform(put("/orders/{id}/complete", inProgressOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(OrderStatus.READY.name()));
    }

    @Test
    void testGetAllOrders() throws Exception {
        Order order1 = new Order();
        Order order2 = new Order();
        orderRepository.save(order1);
        orderRepository.save(order2);

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}