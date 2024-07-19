package com.java.hotel.service.model.purchase;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.java.hotel.service.model.guest.Guest;
import com.java.hotel.service.model.service.Service;
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
@Table(name = "purchase")
@EqualsAndHashCode(of = "id")
@Builder
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "guest_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Guest guest;

    @ManyToOne
    @JoinColumn(name = "service_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Service service;

    @Column(name = "date_start_rental", nullable = false)
    @FutureOrPresent
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateStartRental;

    @Column(name = "date_end_rental", nullable = false)
    @FutureOrPresent
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateEndRental;

    public Purchase(Guest guest, Service service, LocalDate dateStartRental, LocalDate dateEndRental) {
        this.guest = guest;
        this.service = service;
        this.dateStartRental = dateStartRental;
        this.dateEndRental = dateEndRental;
    }
}
