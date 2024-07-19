package com.java.hotel.service.mapper.room;

import com.java.hotel.service.dto.room.RoomDTO;
import com.java.hotel.service.mapper.Mappable;
import com.java.hotel.service.model.room.Room;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomMapper extends Mappable<Room, RoomDTO> {
}

