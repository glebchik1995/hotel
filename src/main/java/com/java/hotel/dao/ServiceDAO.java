package com.java.hotel.dao;

import com.java.hotel.service.model.service.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ServiceDAO extends JpaRepository<Service, Long>, JpaSpecificationExecutor<Service> {

    @Query("SELECT COUNT(s) FROM Service s WHERE s.available = true")
    long countServicesByAvailableTrue();
}
