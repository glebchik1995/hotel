package com.java.hotel.mappers.guest;

import com.java.hotel.config.BaseIntegrationTest;
import com.java.hotel.service.dto.guest.GuestDTO;
import com.java.hotel.service.mapper.guest.GuestMapper;
import com.java.hotel.service.model.guest.Guest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@AutoConfigureMockMvc
class GuestMapperTest extends BaseIntegrationTest {

    @Autowired
    private GuestMapper guestMapper;

    @Test
    void testToDto() {

        Guest guest = Guest.builder()
                .name("Глеб")
                .surname("Глебович")
                .age(28)
                .passport("AС1234569")
                .phone("1888888888")
                .build();

        GuestDTO guestDTO = guestMapper.toDto(guest);

        assertThat(guestDTO.getName(), equalTo(guest.getName()));
        assertThat(guestDTO.getSurname(), equalTo(guest.getSurname()));
        assertThat(guestDTO.getAge(), equalTo(guest.getAge()));
        assertThat(guestDTO.getPassport(), equalTo(guest.getPassport()));
        assertThat(guestDTO.getPhone(), equalTo(guest.getPhone()));
    }

    @Test
    void toEntity() {

        GuestDTO guestDTO = GuestDTO.builder()
                .name("Глеб")
                .surname("Глебович")
                .age(28)
                .passport("AС1234569")
                .phone("1888888888")
                .build();

        Guest guest = guestMapper.toEntity(guestDTO);

        assertThat(guest.getName(), equalTo(guestDTO.getName()));
        assertThat(guest.getSurname(), equalTo(guestDTO.getSurname()));
        assertThat(guest.getAge(), equalTo(guestDTO.getAge()));
        assertThat(guest.getPassport(), equalTo(guestDTO.getPassport()));
        assertThat(guest.getPhone(), equalTo(guestDTO.getPhone()));
    }
}
