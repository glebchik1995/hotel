package com.java.hotel.service.mapper.guest;

import com.java.hotel.service.dto.guest.GuestDTO;
import com.java.hotel.service.mapper.Mappable;
import com.java.hotel.service.model.guest.Guest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GuestMapper extends Mappable<Guest, GuestDTO> {
}
