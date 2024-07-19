package com.java.hotel.service.model.residence;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.java.hotel.service.model.guest.Guest;
import com.java.hotel.service.model.room.Room;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "residence")
@EqualsAndHashCode(of = "id")
@Builder
public class Residence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "guest_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Guest guest;

    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Room room;

    @Column(name = "date_checkin", nullable = false)
    @FutureOrPresent(message = "Дата заезда должна быть в настоящем или будущем.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateCheckIn;

    @Column(name = "date_checkout", nullable = false)
    @FutureOrPresent(message = "Дата выезда должна быть в настоящем или будущем.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateCheckOut;

    public Residence(Guest guest, Room room, LocalDate dateCheckIn, LocalDate dateCheckOut) {
        this.guest = guest;
        this.room = room;
        this.dateCheckIn = dateCheckIn;
        this.dateCheckOut = dateCheckOut;
    }
}
