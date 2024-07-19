package com.java.hotel.mappers.guest;

import com.java.hotel.config.BaseIntegrationTest;
import com.java.hotel.service.dto.guest.GuestAndRoomDTO;
import com.java.hotel.service.mapper.guest.GuestAndRoomMapper;
import com.java.hotel.service.model.guest.Guest;
import com.java.hotel.service.model.residence.Residence;
import com.java.hotel.service.model.room.Room;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.time.LocalDate;

import static com.java.hotel.service.model.room.State.FREE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GuestAndRoomMapperTest extends BaseIntegrationTest {

    @Autowired
    private GuestAndRoomMapper guestAndRoomMapper;

    @Test
    void testToDto() {

        Guest guest = Guest.builder()
                .name("Глеб")
                .surname("Глебович")
                .age(28)
                .passport("AС1234569")
                .phone("1888888888")
                .build();

        Room room = Room.builder()
                .state(FREE)
                .size(2)
                .price(1000)
                .star(1)
                .build();

        Residence residence = Residence.builder()
                .guest(guest)
                .room(room)
                .dateCheckIn(LocalDate.parse("2024-04-22"))
                .dateCheckOut(LocalDate.parse("2024-04-23"))
                .build();

        GuestAndRoomDTO guestAndRoomDTO = guestAndRoomMapper.toDto(residence);

        assertThat(guestAndRoomDTO.getGuest().getName(), equalTo(residence.getGuest().getName()));
        assertThat(guestAndRoomDTO.getGuest().getSurname(), equalTo(residence.getGuest().getSurname()));
        assertThat(guestAndRoomDTO.getGuest().getAge(), equalTo(residence.getGuest().getAge()));
        assertThat(guestAndRoomDTO.getGuest().getPassport(), equalTo(residence.getGuest().getPassport()));
        assertThat(guestAndRoomDTO.getGuest().getPhone(), equalTo(residence.getGuest().getPhone()));

        assertThat(guestAndRoomDTO.getRoom().getState(), equalTo(residence.getRoom().getState()));
        assertThat(guestAndRoomDTO.getRoom().getSize(), equalTo(residence.getRoom().getSize()));
        assertThat(guestAndRoomDTO.getRoom().getPrice(), equalTo(residence.getRoom().getPrice()));
        assertThat(guestAndRoomDTO.getRoom().getStar(), equalTo(residence.getRoom().getStar()));

        assertThat(guestAndRoomDTO.getDateCheckIn(), equalTo(residence.getDateCheckIn()));
        assertThat(guestAndRoomDTO.getDateCheckOut(), equalTo(residence.getDateCheckOut()));
    }
}
