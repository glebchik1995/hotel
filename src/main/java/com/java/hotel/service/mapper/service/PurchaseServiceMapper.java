package com.java.hotel.service.mapper.service;

import com.java.hotel.service.dto.service.PurchaseServiceDTO;
import com.java.hotel.service.dto.service.PurchaseServiceSimpleDTO;
import com.java.hotel.service.mapper.guest.GuestMapper;
import com.java.hotel.service.model.purchase.Purchase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {GuestMapper.class, ServiceMapper.class})
public interface PurchaseServiceMapper {

    @Mapping(source = "guest.id", target = "guestId")
    @Mapping(source = "service", target = "service")
    PurchaseServiceSimpleDTO toSimpleDto(Purchase purchase);

    @Mapping(source = "guest", target = "guest")
    @Mapping(source = "service", target = "service")
    PurchaseServiceDTO toDto(Purchase purchase);
}
