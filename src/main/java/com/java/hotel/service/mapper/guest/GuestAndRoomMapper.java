package com.java.hotel.service.mapper.guest;

import com.java.hotel.service.dto.guest.GuestAndRoomDTO;
import com.java.hotel.service.mapper.room.RoomMapper;
import com.java.hotel.service.model.residence.Residence;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {GuestMapper.class, RoomMapper.class})
public interface GuestAndRoomMapper {

    @Mapping(source = "guest", target = "guest")
    @Mapping(source = "room", target = "room")
    GuestAndRoomDTO toDto(Residence residence);
}
