package com.java.hotel.dao;

import com.java.hotel.service.model.purchase.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PurchaseDAO extends JpaRepository<Purchase, Long> {

    @Query("SELECT SUM(r.price) + COALESCE(SUM(s.price), 0) " +
            "FROM Room r " +
            "LEFT JOIN Residence re ON r.id = re.room.id " +
            "LEFT JOIN Purchase p ON re.guest.id = p.guest.id " +
            "LEFT JOIN Service s ON p.service.id = s.id " +
            "WHERE r.id = :roomId")
    int findTotalCostForByRoomId(@Param(value = "roomId") Long roomId);

    @Query("SELECT p FROM Purchase p WHERE p.guest.id = :guestId")
    Page<Purchase> findPurchaseByGuest_IdOrderBy(@Param("guestId") Long guestId, Pageable pageable);
}
