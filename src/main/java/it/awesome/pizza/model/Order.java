package it.awesome.pizza.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String pizzaType;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING; // Stato iniziale

    private boolean active = false; // Flag per l'ordine attuale
}
