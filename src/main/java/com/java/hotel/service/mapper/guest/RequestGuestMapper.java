package com.java.hotel.service.mapper.guest;

import com.java.hotel.service.dto.guest.RequestGuestDTO;
import com.java.hotel.service.model.guest.Guest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {GuestMapper.class})
public interface RequestGuestMapper {

    @Mapping(target = "day", ignore = true)
    RequestGuestDTO toDto(Guest guest);

    Guest toEntity(RequestGuestDTO requestGuestDTO);

}

