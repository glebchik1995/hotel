package com.java.hotel.service.impl;

import com.java.hotel.dao.GuestDAO;
import com.java.hotel.dao.ResidenceDAO;
import com.java.hotel.dao.RoomDAO;
import com.java.hotel.dao.exception.DataAccessException;
import com.java.hotel.dao.exception.DataNotFoundException;
import com.java.hotel.service.IGuestService;
import com.java.hotel.service.dto.guest.GuestAndRoomDTO;
import com.java.hotel.service.dto.guest.GuestCheckInDTO;
import com.java.hotel.service.dto.guest.GuestDTO;
import com.java.hotel.service.dto.guest.RequestGuestDTO;
import com.java.hotel.service.mapper.guest.GuestAndRoomMapper;
import com.java.hotel.service.mapper.guest.GuestCheckInMapper;
import com.java.hotel.service.mapper.guest.GuestMapper;
import com.java.hotel.service.mapper.guest.RequestGuestMapper;
import com.java.hotel.service.model.guest.Guest;
import com.java.hotel.service.model.residence.Residence;
import com.java.hotel.service.model.room.Room;
import com.java.hotel.service.model.room.State;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static com.java.hotel.service.model.room.State.OCCUPIED;

@Slf4j
@Service
public class GuestService implements IGuestService {

    private final GuestMapper guestMapper;
    private final GuestAndRoomMapper guestAndRoomMapper;
    private final RequestGuestMapper requestGuestMapper;
    private final GuestDAO guestDAO;
    private final RoomDAO roomDAO;
    private final ResidenceDAO residenceDAO;
    private final GuestCheckInMapper guestCheckInMapper;
    private final String roomHistorySize;
    private final String changeRoomStatusEnabled;

    @Autowired
    public GuestService(GuestMapper guestMapper, GuestAndRoomMapper guestAndRoomMapper, RequestGuestMapper requestGuestMapper,
                        GuestDAO guestDAO,
                        RoomDAO roomDAO, ResidenceDAO residenceDAO,
                        GuestCheckInMapper guestCheckInMapper,
                        @Value("${roomHistorySize}") String roomHistorySize,
                        @Value("${changeRoomStatusEnabled}") String changeRoomStatusEnabled) {
        this.guestMapper = guestMapper;
        this.guestAndRoomMapper = guestAndRoomMapper;
        this.requestGuestMapper = requestGuestMapper;
        this.guestDAO = guestDAO;
        this.roomDAO = roomDAO;
        this.residenceDAO = residenceDAO;
        this.guestCheckInMapper = guestCheckInMapper;
        this.roomHistorySize = roomHistorySize;
        this.changeRoomStatusEnabled = changeRoomStatusEnabled;
    }


    @Override
    @Transactional
    public List<GuestCheckInDTO> checkIn(List<RequestGuestDTO> guestsDTO) {

        int day = guestsDTO.getFirst().getDay();

        LocalDate date = LocalDate.now();

        List<Guest> guests = guestsDTO.stream()
                .map(requestGuestMapper::toEntity)
                .toList();

        Room room = roomDAO.findRoomsByStateAndSize(guests.size(), State.FREE).stream().findFirst()
                .orElseThrow(() -> new DataNotFoundException("Номера с такими критериями на данный момент нет в наличии!"));

        if (changeRoomStatusEnabled.equals("true")) {
            for (Guest guest : guests) {
                guestDAO.save(guest);
                addClientToHistory(guest, room);
                room.setState(OCCUPIED);
                roomDAO.save(room);

                Residence residence = new Residence(guest, room, date, date.plusDays(day));
                residenceDAO.save(residence);
            }
        } else {
            throw new DataAccessException("Комната с идентификатором " + room.getId() + " не может быть забронирована!");
        }

        return residenceDAO.findAllByRoomId(room.getId()).stream()
                .map(guestCheckInMapper::toDto)
                .toList();
    }


    @Override
    @Transactional
    public void checkOut(Long roomId) {

        Room room = roomDAO.findById(roomId).orElseThrow(()
                -> new DataNotFoundException("Комната с идентификатором " + roomId + " не найдена"));

        List<Guest> guests = guestDAO.findGuestsByRoomId(room.getId());

        if (!guests.isEmpty()) {
            guestDAO.deleteAll(guests);
        } else {
            log.info("Гостиничном номер №{} не заселен", roomId);
        }

        room.setState(State.FREE);
        roomDAO.save(room);

    }

    @Override
    public GuestDTO findById(Long guestId) {
        Guest guest = guestDAO.findById(guestId).orElseThrow(() ->
                new DataNotFoundException(String.format("Гость с идентификатором %d не найден", guestId)));
        return guestMapper.toDto(guest);
    }

    @Override
    public Page<GuestCheckInDTO> getAllGuests(Pageable pageable) {
        Page<Residence> residencePage = residenceDAO.findAll(pageable);
        return residencePage.map(guestCheckInMapper::toDto);
    }

    @Override
    public long getCountCheckedInGuest() {
        return residenceDAO.countResidence();
    }

    @Override
    public Page<GuestDTO> getHistoryGuestsForAllTime(Pageable pageable) {
        Page<Guest> historyGuests = roomDAO.findAllGuestHistory(pageable);
        return historyGuests.map(guestMapper::toDto);
    }

    @Override
    public Page<GuestDTO> getHistoryGuestsByRoomId(Long roomId, Pageable pageable) {
        Page<Guest> historyGuests = roomDAO.findGuestHistoryByRoomId(roomId, pageable);
        return historyGuests.map(guestMapper::toDto);
    }

    @Override
    public Page<GuestAndRoomDTO> getGuestsAndTheirRooms(Pageable pageable) {
        Page<Residence> residencePage = residenceDAO.findAll(pageable);
        return residencePage.map(guestAndRoomMapper::toDto);
    }

    @Override
    public List<GuestCheckInDTO> getLast3GuestsToCheckIn() {
        List<Residence> residencePage = residenceDAO.findAll().stream()
                .limit(3)
                .toList();

        return residencePage.stream()
                .map(guestCheckInMapper::toDto)
                .toList();
    }

    private void addClientToHistory(Guest guest, Room room) {
        Set<Guest> historyGuests = room.getHistoryGuests();
        try {
            int historySize = Integer.parseInt(roomHistorySize);
            if (historyGuests.size() >= historySize) {
                Iterator<Guest> iterator = historyGuests.iterator();
                if (iterator.hasNext()) {
                    iterator.next();
                    iterator.remove();
                }
            }
            historyGuests.add(guest);
            room.setHistoryGuests(historyGuests);
            roomDAO.save(room);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Не удалось преобразовать roomHistorySize в число!");
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Ошибка при итерации по хранилищу истории гостей!");
        }
    }
}
