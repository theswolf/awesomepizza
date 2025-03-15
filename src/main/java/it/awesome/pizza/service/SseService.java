package it.awesome.pizza.service;

import it.awesome.pizza.model.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseService {

    @Getter
    private final Map<Long, List<SseEmitter>> orderEmitters = new ConcurrentHashMap<>();

    @Getter
    private final List<SseEmitter> pizzaioloEmitters = new CopyOnWriteArrayList<>();

    public List<SseEmitter> getAllEmitters(Long orderId) {
        List<SseEmitter> allEmitters = new CopyOnWriteArrayList<>(pizzaioloEmitters);
        List<SseEmitter> orderSpecificEmitters = orderEmitters.get(orderId);
        if (orderSpecificEmitters != null) {
            allEmitters.addAll(orderSpecificEmitters);
        }
        return allEmitters;
    }

    public void notifyClient(Long orderId, Order order) {
        List<SseEmitter> emitters = getAllEmitters(orderId);
        if (emitters != null) {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event().data(order));
                } catch (IOException e) {
                    emitter.complete();
                    emitters.remove(emitter);
                }
            }
        }

    }

    public void removeEmitter(Long orderId, SseEmitter emitter) {
        List<SseEmitter> emitters = orderEmitters.get(orderId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                orderEmitters.remove(orderId);
            }
        }
    }
}
