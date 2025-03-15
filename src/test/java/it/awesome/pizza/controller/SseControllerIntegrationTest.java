package it.awesome.pizza.controller;

import it.awesome.pizza.service.SseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SseService sseService;

    @BeforeEach
    void setUp() {
        sseService.getOrderEmitters().clear();
        sseService.getPizzaioloEmitters().clear();
    }

    @Test
    void testStreamOrders() throws Exception {
        mockMvc.perform(get("/sse/orders"))
                .andExpect(status().isOk());
    }

    @Test
    void testSubscribeToOrder() throws Exception {
        Long orderId = 1L;

        mockMvc.perform(get("/sse/orders/{orderId}", orderId))
                .andExpect(status().isOk());

        List<SseEmitter> emitters = sseService.getOrderEmitters().get(orderId);
        assertNotNull(emitters);
        assertEquals(1, emitters.size());
    }
}