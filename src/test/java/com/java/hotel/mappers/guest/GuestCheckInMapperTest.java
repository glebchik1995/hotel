package com.java.hotel.mappers.guest;

import com.java.hotel.config.BaseIntegrationTest;
import com.java.hotel.service.dto.guest.GuestCheckInDTO;
import com.java.hotel.service.mapper.guest.GuestCheckInMapper;
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
class GuestCheckInMapperTest extends BaseIntegrationTest {

    @Autowired
    private GuestCheckInMapper guestCheckInMapper;

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

        GuestCheckInDTO guestCheckInDTO = guestCheckInMapper.toDto(residence);

        assertThat(guestCheckInDTO.getGuest().getName(), equalTo(residence.getGuest().getName()));
        assertThat(guestCheckInDTO.getGuest().getSurname(), equalTo(residence.getGuest().getSurname()));
        assertThat(guestCheckInDTO.getGuest().getAge(), equalTo(residence.getGuest().getAge()));
        assertThat(guestCheckInDTO.getGuest().getPassport(), equalTo(residence.getGuest().getPassport()));
        assertThat(guestCheckInDTO.getGuest().getPhone(), equalTo(residence.getGuest().getPhone()));

        assertThat(guestCheckInDTO.getDateCheckIn(), equalTo(residence.getDateCheckIn()));
        assertThat(guestCheckInDTO.getDateCheckOut(), equalTo(residence.getDateCheckOut()));
    }
}
