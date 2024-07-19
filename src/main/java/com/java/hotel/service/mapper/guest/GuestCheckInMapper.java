package com.java.hotel.service.mapper.guest;

import com.java.hotel.service.dto.guest.GuestCheckInDTO;
import com.java.hotel.service.model.residence.Residence;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = {GuestMapper.class})
public interface GuestCheckInMapper {

    @Mapping(source = "guest", target = "guest")
    GuestCheckInDTO toDto(Residence residence);

}