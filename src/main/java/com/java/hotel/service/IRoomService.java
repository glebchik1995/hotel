package com.java.hotel.service;

import com.java.hotel.service.dto.room.RequestUpdateRoomPriceDTO;
import com.java.hotel.service.dto.room.RoomDTO;
import com.java.hotel.service.filter.CriteriaModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface IRoomService {

    RoomDTO addRoom(RoomDTO room);

    Page<RoomDTO> getRooms(CriteriaModel criteriaModel, Pageable pageable);

    Page<RoomDTO> getRoomsOnDate(LocalDate date, Pageable pageable);

    void deleteRoom(Long roomId);

    RoomDTO updatePriceRoom(RequestUpdateRoomPriceDTO dto);

    RoomDTO closeRoomForRepairs(Long roomId);

    RoomDTO openRoomAfterRepairs(Long roomId);

    long getCountFreeRooms();

    RoomDTO getRoomById(Long roomId);

    int getTotalCostForRoom(Long roomId);

}
