package it.awesome.pizza.controller;

import it.awesome.pizza.model.Order;
import it.awesome.pizza.model.OrderStatus;
import it.awesome.pizza.service.OrderService;
import it.awesome.pizza.service.SseService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sse")
public class SseController {

    private final SseService sseService;

    public SseController(SseService sseService) {
        this.sseService = sseService;
    }

    @GetMapping(value = "/orders", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamOrders() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        sseService.getPizzaioloEmitters().add(emitter);
        emitter.onCompletion(() -> sseService.getPizzaioloEmitters().remove(emitter));
        emitter.onTimeout(() -> sseService.getPizzaioloEmitters().remove(emitter));
        return emitter;
    }

    @GetMapping(value = "/orders/{orderId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeToOrder(@PathVariable Long orderId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        sseService.getOrderEmitters().computeIfAbsent(orderId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> sseService.removeEmitter(orderId, emitter));
        emitter.onTimeout(() -> sseService.removeEmitter(orderId, emitter));

        return emitter;
    }


}
