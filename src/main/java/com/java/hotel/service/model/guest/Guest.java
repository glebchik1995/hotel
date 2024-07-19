package com.java.hotel.service.model.guest;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "guest")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "age", nullable = false)
    private int age;

    @Column(name = "passport", nullable = false)
    private String passport;

    @Column(name = "phone", nullable = false)
    private String phone;

    public Guest(String name, String surname, int age, String passport, String phone) {
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.passport = passport;
        this.phone = phone;
    }
}
