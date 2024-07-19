package com.java.hotel.service.model.room;

import com.java.hotel.service.model.guest.Guest;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "room")
@EqualsAndHashCode(of = "id")
@Builder
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private State state;

    @Column(name = "size", nullable = false)
    private int size;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "star", nullable = false)
    private int star;

    @OneToMany
    @JoinTable(
            name = "guest_history",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "guest_id")
    )
    private Set<Guest> historyGuests = new HashSet<>();

    public Room(State state, int size, int price, int star) {
        this.state = state;
        this.size = size;
        this.price = price;
        this.star = star;
    }

    public Room(Long id, State state, int size, int price, int star) {
        this.id = id;
        this.state = state;
        this.size = size;
        this.price = price;
        this.star = star;
    }
}
