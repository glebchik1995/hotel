package com.java.hotel.service;

import com.java.hotel.service.dto.guest.GuestAndRoomDTO;
import com.java.hotel.service.dto.guest.GuestCheckInDTO;
import com.java.hotel.service.dto.guest.GuestDTO;
import com.java.hotel.service.dto.guest.RequestGuestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IGuestService {

    List<GuestCheckInDTO> checkIn(List<RequestGuestDTO> guestDTOS);

    void checkOut(Long roomId);

    GuestDTO findById(Long guestId);

    Page<GuestCheckInDTO> getAllGuests(Pageable pageable);

    Page<GuestDTO> getHistoryGuestsForAllTime(Pageable pageable);

    Page<GuestDTO> getHistoryGuestsByRoomId(Long roomId, Pageable pageable);

    Page<GuestAndRoomDTO> getGuestsAndTheirRooms(Pageable pageable);

    List<GuestCheckInDTO> getLast3GuestsToCheckIn();

    long getCountCheckedInGuest();
}

