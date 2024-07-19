package com.java.hotel.service;

import com.java.hotel.dao.GuestDAO;
import com.java.hotel.dao.ResidenceDAO;
import com.java.hotel.dao.RoomDAO;
import com.java.hotel.dao.exception.DataAccessException;
import com.java.hotel.dao.exception.DataNotFoundException;
import com.java.hotel.service.dto.guest.GuestAndRoomDTO;
import com.java.hotel.service.dto.guest.GuestCheckInDTO;
import com.java.hotel.service.dto.guest.GuestDTO;
import com.java.hotel.service.dto.guest.RequestGuestDTO;
import com.java.hotel.service.impl.GuestService;
import com.java.hotel.service.mapper.guest.GuestAndRoomMapper;
import com.java.hotel.service.mapper.guest.GuestCheckInMapper;
import com.java.hotel.service.mapper.guest.GuestMapper;
import com.java.hotel.service.mapper.guest.RequestGuestMapper;
import com.java.hotel.service.model.guest.Guest;
import com.java.hotel.service.model.residence.Residence;
import com.java.hotel.service.model.room.Room;
import com.java.hotel.service.model.room.State;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.java.hotel.service.model.room.State.FREE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuestServiceTest {

    @InjectMocks
    private GuestService guestService;

    @Mock
    private GuestDAO guestDAO;

    @Mock
    private RoomDAO roomDAO;

    @Mock
    private ResidenceDAO residenceDAO;

    @Mock
    private RequestGuestMapper requestGuestMapper;

    @Mock
    private GuestMapper guestMapper;

    @Mock
    private GuestAndRoomMapper guestAndRoomMapper;

    @Mock
    private GuestCheckInMapper guestCheckInMapper;

    @Test
    public void testCheckIn() {

        LocalDate currentDate = LocalDate.now();

        RequestGuestDTO guestDTO = mock(RequestGuestDTO.class);

        List<RequestGuestDTO> guestsDTO = new ArrayList<>();
        guestsDTO.add(guestDTO);

        Room room = new Room(1L, FREE, 1, 2000, 3);

        Guest guest = mock(Guest.class);

        Residence residence = new Residence(guest, room, currentDate, currentDate.plusDays(5));

        when(requestGuestMapper.toEntity(any(RequestGuestDTO.class))).thenReturn(guest);
        when(roomDAO.findRoomsByStateAndSize(guestsDTO.size(), FREE)).thenReturn(List.of(room));
        when(guestDAO.save(any(Guest.class))).thenReturn(guest);
        when(roomDAO.save(any(Room.class))).thenReturn(room);
        when(residenceDAO.save(any(Residence.class))).thenReturn(residence);
        when(residenceDAO.findAllByRoomId(room.getId())).thenReturn(List.of(residence));

        ReflectionTestUtils.setField(guestService, "roomHistorySize", "10");
        ReflectionTestUtils.setField(guestService, "changeRoomStatusEnabled", "true");

        List<GuestCheckInDTO> result = guestService.checkIn(guestsDTO);
        assertThat(result).isNotEmpty();
        verify(requestGuestMapper, times(1)).toEntity(guestDTO);
        verify(roomDAO, times(1)).findRoomsByStateAndSize(guestsDTO.size(), State.FREE);
        verify(guestDAO, times(1)).save(guest);
        verify(roomDAO, times(2)).save(room);
        verify(residenceDAO, times(1)).save(any(Residence.class));
        verify(residenceDAO, times(1)).findAllByRoomId(room.getId());
    }

    @Test
    public void testCheckOut() {
        Long roomId = 1L;

        Room room = new Room(1L, FREE, 1, 2000, 3);

        List<Guest> guests = new ArrayList<>();
        guests.add(new Guest());

        when(roomDAO.findById(roomId)).thenReturn(Optional.of(room));
        when(guestDAO.findGuestsByRoomId(roomId)).thenReturn(guests);

        guestService.checkOut(roomId);

        verify(roomDAO, times(1)).findById(roomId);
        verify(guestDAO, times(1)).findGuestsByRoomId(roomId);
        verify(guestDAO, times(1)).deleteAll(guests);

        assertEquals(State.FREE, room.getState());
        verify(roomDAO, times(1)).save(room);
    }

    @Test
    public void testCheckOut_DataNotFoundException() {
        Long roomId = 1L;

        when(roomDAO.findById(roomId)).thenReturn(java.util.Optional.empty());

        assertThrows(DataNotFoundException.class, () -> guestService.checkOut(roomId));

        verify(guestDAO, never()).findGuestsByRoomId(anyLong());
        verify(guestDAO, never()).deleteAll(any());
    }

    @Test
    public void testCheckIn_DataNotFoundException() {
        List<RequestGuestDTO> guestsDTO = new ArrayList<>();
        guestsDTO.add(new RequestGuestDTO());
        assertThrows(DataNotFoundException.class, () -> guestService.checkIn(guestsDTO));
        verify(guestDAO, never()).save(any());
        verify(roomDAO, never()).save(any());
        verify(residenceDAO, never()).save(any());
        verify(guestCheckInMapper, never()).toDto(any());

    }

    @Test
    public void testCheckIn_DataAccessException() {
        List<RequestGuestDTO> guestsDTO = new ArrayList<>();
        RequestGuestDTO guestDTO = new RequestGuestDTO();
        guestsDTO.add(guestDTO);

        when(roomDAO.findRoomsByStateAndSize(anyInt(), any(State.class))).thenReturn(List.of(new Room()));
        ReflectionTestUtils.setField(guestService, "changeRoomStatusEnabled", "false");

        assertThrows(DataAccessException.class, () -> guestService.checkIn(guestsDTO));
    }

    @Test
    public void testAddClientToHistory_NumberFormatException() {

        RequestGuestDTO guestDTO = mock(RequestGuestDTO.class);

        List<RequestGuestDTO> guestsDTO = new ArrayList<>();
        guestsDTO.add(guestDTO);

        Room room = mock(Room.class);
        Guest guest = mock(Guest.class);

        when(requestGuestMapper.toEntity(any(RequestGuestDTO.class))).thenReturn(guest);
        when(roomDAO.findRoomsByStateAndSize(guestsDTO.size(), FREE)).thenReturn(List.of(room));

        ReflectionTestUtils.setField(guestService, "roomHistorySize", "not_a_number");
        ReflectionTestUtils.setField(guestService, "changeRoomStatusEnabled", "true");

        assertThrows(NumberFormatException.class, () -> guestService.checkIn(guestsDTO));
    }

    @Test
    void testFindById() {
        Long guestId = 1L;
        Guest room = mock(Guest.class);
        GuestDTO guestDTO = mock(GuestDTO.class);
        when(guestDAO.findById(anyLong())).thenReturn(Optional.of(room));
        when(guestMapper.toDto(any(Guest.class))).thenReturn(guestDTO);
        GuestDTO result = guestService.findById(guestId);
        assertThat(result).isNotNull();
        assertEquals(guestDTO, result);
        verify(guestDAO, times(1)).findById(guestId);
        verify(guestMapper, times(1)).toDto(any(Guest.class));
    }

    @Test
    void testGetAllGuests() {
        Residence guest1 = new Residence();
        Residence guest2 = new Residence();
        List<Residence> guests = Arrays.asList(guest1, guest2);
        Page<Residence> page = new PageImpl<>(guests);

        when(residenceDAO.findAll(any(Pageable.class))).thenReturn(page);

        Page<Residence> result = residenceDAO.findAll(PageRequest.of(0, 10));

        assertEquals(2, result.getTotalElements());

        verify(residenceDAO, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testGetCountCheckedInGuest() {
        long expectedCount = 5L;
        when(guestService.getCountCheckedInGuest()).thenReturn(expectedCount);
        long result = guestService.getCountCheckedInGuest();
        assertEquals(expectedCount, result);
        verify(residenceDAO, times(1)).countResidence();
    }

    @Test
    void testGetHistoryGuestsForAllTime() {

        Guest guest1 = mock(Guest.class);
        Guest guest2 = mock(Guest.class);
        List<Guest> guests = Arrays.asList(guest1, guest2);
        Page<Guest> page = new PageImpl<>(guests);

        when(roomDAO.findAllGuestHistory(any(Pageable.class))).thenReturn(page);

        Page<GuestDTO> result = guestService.getHistoryGuestsForAllTime(PageRequest.of(0, 10));

        assertEquals(2, result.getTotalElements());

        verify(roomDAO, times(1)).findAllGuestHistory(any(Pageable.class));
        verify(guestMapper, times(2)).toDto(any(Guest.class));
    }

    @Test
    void testGetHistoryGuestsByRoomId() {

        Guest guest1 = mock(Guest.class);
        Guest guest2 = mock(Guest.class);
        List<Guest> guests = Arrays.asList(guest1, guest2);
        Page<Guest> page = new PageImpl<>(guests);

        when(roomDAO.findGuestHistoryByRoomId(anyLong(), any(Pageable.class))).thenReturn(page);

        Page<GuestDTO> result = guestService.getHistoryGuestsByRoomId(1L, PageRequest.of(0, 10));
        assertEquals(2, result.getTotalElements());

        verify(roomDAO, times(1)).findGuestHistoryByRoomId(anyLong(), any(Pageable.class));
        verify(guestMapper, times(2)).toDto(any(Guest.class));
    }

    @Test
    void testGetGuestsAndTheirRooms() {

        Residence guest1 = mock(Residence.class);
        Residence guest2 = mock(Residence.class);
        List<Residence> guests = Arrays.asList(guest1, guest2);
        Page<Residence> page = new PageImpl<>(guests);

        when(residenceDAO.findAll(any(Pageable.class))).thenReturn(page);
        when(guestAndRoomMapper.toDto(any(Residence.class))).thenReturn(any(GuestAndRoomDTO.class));

        Page<GuestAndRoomDTO> result = guestService.getGuestsAndTheirRooms(PageRequest.of(0, 10));
        assertEquals(2, result.getTotalElements());

        verify(residenceDAO, times(1)).findAll(any(Pageable.class));
        verify(guestAndRoomMapper, times(2)).toDto(any(Residence.class));
    }

    @Test
    public void testGetLast3GuestsToCheckIn() {
        Residence guest1 = mock(Residence.class);
        Residence guest2 = mock(Residence.class);
        Residence guest3 = mock(Residence.class);
        List<Residence> guests = Arrays.asList(guest1, guest2, guest3);

        when(residenceDAO.findAll()).thenReturn(guests);
        when(guestCheckInMapper.toDto(any(Residence.class))).thenReturn(new GuestCheckInDTO());

        List<GuestCheckInDTO> result = guestService.getLast3GuestsToCheckIn();

        assertEquals(3, result.size());

        verify(residenceDAO, times(1)).findAll();
        verify(guestCheckInMapper, times(3)).toDto(any(Residence.class));
    }
}