package com.java.hotel.dao;


import com.java.hotel.service.model.residence.Residence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResidenceDAO extends JpaRepository<Residence, Long> {

    @Query("SELECT COUNT(r) FROM Residence r")
    long countResidence();

    @Query("SELECT r FROM Residence r WHERE r.room.id = :roomId")
    List<Residence> findAllByRoomId(@Param(value = "roomId") Long roomId);

}

