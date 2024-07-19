package com.java.hotel.service;

import com.java.hotel.dao.RoomDAO;
import com.java.hotel.dao.exception.*;
import com.java.hotel.service.dto.room.RequestUpdateRoomPriceDTO;
import com.java.hotel.service.dto.room.RoomDTO;
import com.java.hotel.service.impl.RoomService;
import com.java.hotel.service.mapper.room.RoomMapper;
import com.java.hotel.service.model.room.Room;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static com.java.hotel.service.model.room.State.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomDAO roomDao;

    @Mock
    private RoomMapper roomMapper;

    @InjectMocks
    private RoomService roomService;

    @Test
    public void testAddRoom() {
        Room room = mock(Room.class);
        RoomDTO roomDTO = mock(RoomDTO.class);

        when(roomMapper.toEntity(any(RoomDTO.class))).thenReturn(room);
        when(roomDao.save(any(Room.class))).thenReturn(room);
        when(roomMapper.toDto(any(Room.class))).thenReturn(roomDTO);

        RoomDTO result = roomService.addRoom(roomDTO);

        assertEquals(roomDTO, result);

        verify(roomMapper, times(1)).toEntity(any(RoomDTO.class));
        verify(roomDao, times(1)).save(any(Room.class));
        verify(roomMapper, times(1)).toDto(any(Room.class));
    }

    @Test
    public void testGetRoomById() {
        Long roomId = 1L;
        Room room = mock(Room.class);
        RoomDTO roomDTO = mock(RoomDTO.class);
        when(roomDao.findById(anyLong())).thenReturn(Optional.of(room));
        when(roomMapper.toDto(any(Room.class))).thenReturn(roomDTO);
        RoomDTO result = roomService.getRoomById(roomId);
        assertThat(result).isNotNull();
        assertEquals(roomDTO, result);
        verify(roomDao, times(1)).findById(roomId);
        verify(roomMapper, times(1)).toDto(any(Room.class));
    }

    @Test
    public void testGetRoomById_ThrowsDataNotFoundException() {
        assertThrows(DataNotFoundException.class, () -> roomService.getRoomById(100000L));
        verify(roomMapper, never()).toDto(any(Room.class));
    }

    @Test
    public void testDeleteRoom() {
        Room room = new Room(FREE, 1, 2000, 3);
        when(roomDao.findById(anyLong())).thenReturn(Optional.of(room));
        roomService.deleteRoom(anyLong());
        verify(roomDao, times(1)).findById(anyLong());
        verify(roomDao, times(1)).deleteById(anyLong());
    }

    @Test
    public void testDeleteRoom_ThrowsDataDeleteException_And_ThrowsDataNotFoundException() {
        Long roomId = 1L;
        Room room = new Room(FREE, 1, 2000, 3);
        room.setState(OCCUPIED);
        when(roomDao.findById(roomId)).thenReturn(Optional.of(room));
        assertThrows(DataDeleteException.class, () -> roomService.deleteRoom(roomId));
        assertThrows(DataNotFoundException.class, () -> roomService.deleteRoom(10000L));
        verify(roomDao, never()).deleteById(anyLong());
    }

    @Test
    public void testUpdatePriceRoom() {
        Room room = new Room(1L, FREE, 1, 2000, 3);
        RoomDTO roomDTO = new RoomDTO(1L, FREE, 1, 200, 3);
        RequestUpdateRoomPriceDTO dto = new RequestUpdateRoomPriceDTO(1L, 200);
        when(roomDao.findById(anyLong())).thenReturn(Optional.of(room));
        when(roomDao.save(any(Room.class))).thenReturn(room);
        when(roomMapper.toDto(any(Room.class))).thenReturn(roomDTO);
        RoomDTO result = roomService.updatePriceRoom(dto);
        assertThat(result).isNotNull();
        assertEquals(roomDTO, result);
        assertEquals(result.getPrice(), dto.getPrice());
        verify(roomDao, times(1)).findById(anyLong());
        verify(roomDao, times(1)).save(any(Room.class));
        verify(roomMapper, times(1)).toDto(any(Room.class));
    }

    @Test
    public void testUpdatePriceRoom_ThrowsDataSaveException() {
        Long roomId = 1L;
        Room room = new Room(1L, OCCUPIED, 1, 2000, 3);
        RequestUpdateRoomPriceDTO dto = new RequestUpdateRoomPriceDTO(1L, 200);
        when(roomDao.findById(roomId)).thenReturn(Optional.of(room));
        assertThrows(DataSaveException.class, () -> roomService.updatePriceRoom(dto));
        verify(roomDao, never()).save(any(Room.class));
        verify(roomMapper, never()).toDto(any(Room.class));
    }

    @Test
    public void testCloseRoomForRepairs() {
        Room room = new Room(1L, FREE, 1, 2000, 3);
        RoomDTO roomDTO = new RoomDTO(1L, BEING_REPAIRED, 1, 2000, 3);
        ReflectionTestUtils.setField(roomService, "changeRoomStatusEnabled", "true");
        when(roomDao.findById(anyLong())).thenReturn(Optional.of(room));
        when(roomDao.save(any(Room.class))).thenReturn(room);
        when(roomMapper.toDto(any(Room.class))).thenReturn(roomDTO);
        RoomDTO result = roomService.closeRoomForRepairs(anyLong());
        assertThat(result).isNotNull();
        assertEquals(roomDTO, result);
        assertEquals(result.getState(), BEING_REPAIRED);
        verify(roomDao, times(1)).findById(anyLong());
        verify(roomDao, times(1)).save(any(Room.class));
        verify(roomMapper, times(1)).toDto(any(Room.class));
    }

    @Test
    public void testCloseRoomForRepairs_ThrowsDataUpdateException() {
        assertThrows(DataUpdateException.class, () -> roomService.closeRoomForRepairs(1L));
        verify(roomDao, never()).save(any(Room.class));
        verify(roomMapper, never()).toDto(any(Room.class));
    }

    @Test
    public void testCloseRoomForRepairs_ThrowsDataAccessException() {
        Long roomId = 1L;
        Room room = new Room(1L, OCCUPIED, 1, 2000, 3);
        ReflectionTestUtils.setField(roomService, "changeRoomStatusEnabled", "true");
        when(roomDao.findById(roomId)).thenReturn(Optional.of(room));
        assertThrows(DataAccessException.class, () -> roomService.closeRoomForRepairs(1L));
    }

    @Test
    public void testCloseRoomForRepairs_ThrowsDataUpdateExceptionWhereStatusAlreadyEqBEING_REPAIRED() {
        Long roomId = 1L;
        Room room = new Room(1L, BEING_REPAIRED, 1, 2000, 3);
        ReflectionTestUtils.setField(roomService, "changeRoomStatusEnabled", "true");
        when(roomDao.findById(roomId)).thenReturn(Optional.of(room));
        assertThrows(DataUpdateException.class, () -> roomService.closeRoomForRepairs(1L));
    }

    @Test
    public void testOpenRoomAfterRepairs() {
        Room room = new Room(1L, BEING_REPAIRED, 1, 2000, 3);
        RoomDTO roomDTO = new RoomDTO(1L, FREE, 1, 2000, 3);
        ReflectionTestUtils.setField(roomService, "changeRoomStatusEnabled", "true");
        when(roomDao.findById(anyLong())).thenReturn(Optional.of(room));
        when(roomDao.save(any(Room.class))).thenReturn(room);
        when(roomMapper.toDto(any(Room.class))).thenReturn(roomDTO);
        RoomDTO result = roomService.openRoomAfterRepairs(anyLong());
        assertThat(result).isNotNull();
        assertEquals(roomDTO, result);
        assertEquals(result.getState(), FREE);
        verify(roomDao, times(1)).findById(anyLong());
        verify(roomDao, times(1)).save(any(Room.class));
        verify(roomMapper, times(1)).toDto(any(Room.class));
    }

    @Test
    void testGetCountAvailableService() {
        long expectedCount = 5L;
        when(roomService.getCountFreeRooms()).thenReturn(expectedCount);
        long result = roomService.getCountFreeRooms();
        assertEquals(expectedCount, result);
        verify(roomDao, times(1)).countRoomsByState_Free();
    }

    @Test
    public void testGetRoomsOnDate() {
        LocalDate date = LocalDate.of(2022, 10, 15);
        Pageable pageable = PageRequest.of(0, 10);
        Room room = mock(Room.class);
        Page<Room> mockRooms = new PageImpl<>(Collections.singletonList(room));
        when(roomDao.findAll(any(Specification.class), eq(pageable))).thenReturn(mockRooms);
        // Вызываем метод, который тестируем
        Page<RoomDTO> expectedRoomDTOs = mockRooms.map(roomMapper::toDto);
        Page<RoomDTO> result = roomService.getRoomsOnDate(date, pageable);
        // Проверяем, что методы были вызваны с нужными параметрами
        verify(roomDao).findAll(any(Specification.class), eq(pageable));
        // Проверяем, что результат соответствует ожидаемому
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedRoomDTOs);
    }

}
