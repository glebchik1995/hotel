package com.java.hotel.dao;

import com.java.hotel.service.model.guest.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GuestDAO extends JpaRepository<Guest, Long> {

    @Modifying
    @Query(value = "UPDATE Room r SET r.state = 'FREE' WHERE r.id IN (SELECT res.room.id FROM Residence res WHERE res.guest.id IN (SELECT g.id FROM Guest g))")
    void deleteAll();

    @Query("SELECT g FROM Guest g INNER JOIN Residence r ON g.id = r.guest.id WHERE r.room.id = :roomId")
    List<Guest> findGuestsByRoomId(@Param("roomId") Long roomId);
}
