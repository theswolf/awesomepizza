package it.awesome.pizza.service;

import it.awesome.pizza.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SseServiceIntegrationTest {

    @Autowired
    private SseService sseService;

    @BeforeEach
    void setUp() {
        sseService.getOrderEmitters().clear();
        sseService.getPizzaioloEmitters().clear();
    }

    @Test
    void testNotifyClient() throws IOException {
        Long orderId = 1L;
        Order order = new Order();
        SseEmitter emitter = new SseEmitter();
        sseService.getOrderEmitters().put(orderId, new CopyOnWriteArrayList<>(List.of(emitter)));
        sseService.getPizzaioloEmitters().add(emitter);

        sseService.notifyClient(orderId, order);

        // Verify that the emitter received the event
        assertNotNull(emitter);
    }

    @Test
    void testRemoveEmitter() {
        Long orderId = 1L;
        SseEmitter emitter = new SseEmitter();
        sseService.getOrderEmitters().put(orderId, new CopyOnWriteArrayList<>(List.of(emitter)));

        sseService.removeEmitter(orderId, emitter);

        assertTrue(sseService.getOrderEmitters().isEmpty());
    }

    @Test
    void testGetAllEmitters() {
        Long orderId = 1L;
        SseEmitter emitter1 = new SseEmitter();
        SseEmitter emitter2 = new SseEmitter();
        sseService.getOrderEmitters().put(orderId, new CopyOnWriteArrayList<>(List.of(emitter1)));
        sseService.getPizzaioloEmitters().add(emitter2);

        List<SseEmitter> allEmitters = sseService.getAllEmitters(orderId);

        assertEquals(2, allEmitters.size());
        assertTrue(allEmitters.contains(emitter1));
        assertTrue(allEmitters.contains(emitter2));
    }
}