package com.java.hotel.dao;


import com.java.hotel.service.model.guest.Guest;
import com.java.hotel.service.model.room.Room;
import com.java.hotel.service.model.room.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomDAO extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {

    @Query("SELECT COUNT(r) FROM Room r WHERE r.state = 'FREE'")
    long countRoomsByState_Free();

    @Query("SELECT DISTINCT h FROM Room r JOIN r.historyGuests h")
    Page<Guest> findAllGuestHistory(Pageable pageable);

    @Query("SELECT g FROM Room r JOIN r.historyGuests g WHERE r.id = :roomId")
    Page<Guest> findGuestHistoryByRoomId(@Param("roomId") Long roomId, Pageable pageable);

    @Query("SELECT r FROM Room r WHERE r.state = :state AND r.size = :guestsSize")
    List<Room> findRoomsByStateAndSize(@Param("guestsSize") int guestsSize, @Param("state") State state);

    @Query("SELECT r FROM Room r WHERE r.state = :state AND r.id = :roomId")
    Room findRoomsByState_OccupiedAndId(@Param("roomId") Long roomId, @Param("state") State state);

}
