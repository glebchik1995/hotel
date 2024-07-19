package com.java.hotel.service.model.service;

import jakarta.persistence.*;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "service")
@EqualsAndHashCode(of = "id")
@Builder
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "price", nullable = false)
    private int price;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "available", nullable = false)
    private boolean available = true;

    public Service(String title, int price, Category category, boolean available) {
        this.title = title;
        this.price = price;
        this.category = category;
        this.available = available;
    }
}
