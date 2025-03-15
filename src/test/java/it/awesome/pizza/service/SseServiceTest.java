// src/test/java/it/awesome/pizza/service/SseServiceTest.java
package it.awesome.pizza.service;

import it.awesome.pizza.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class SseServiceTest {

    private SseService sseService;

    @BeforeEach
    void setUp() {
        sseService = new SseService();
    }

    @Test
    void testNotifyClient() throws IOException {
        Long orderId = 1L;
        Order order = new Order();
        SseEmitter emitter = mock(SseEmitter.class);
        sseService.getOrderEmitters().put(orderId, new CopyOnWriteArrayList<>(List.of(emitter)));
        sseService.getPizzaioloEmitters().add(emitter);

        sseService.notifyClient(orderId, order);

        verify(emitter, times(2)).send(any(SseEmitter.SseEventBuilder.class));
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