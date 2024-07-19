package com.java.hotel.mappers.guest;

import com.java.hotel.config.BaseIntegrationTest;
import com.java.hotel.service.dto.guest.RequestGuestDTO;
import com.java.hotel.service.mapper.guest.RequestGuestMapper;
import com.java.hotel.service.model.guest.Guest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@AutoConfigureMockMvc
class RequestGuestMapperTest extends BaseIntegrationTest {

    @Autowired
    private RequestGuestMapper requestGuestMapper;

    @Test
    void testToDto() {

        Guest guest = Guest.builder()
                .name("Глеб")
                .surname("Глебович")
                .age(28)
                .passport("AС1234569")
                .phone("1888888888")
                .build();

        RequestGuestDTO requestGuestDTO = requestGuestMapper.toDto(guest);

        assertThat(requestGuestDTO.getName(), equalTo(guest.getName()));
        assertThat(requestGuestDTO.getSurname(), equalTo(guest.getSurname()));
        assertThat(requestGuestDTO.getAge(), equalTo(guest.getAge()));
        assertThat(requestGuestDTO.getPassport(), equalTo(guest.getPassport()));
        assertThat(requestGuestDTO.getPhone(), equalTo(guest.getPhone()));
    }

    @Test
    void testToEntity() {

        RequestGuestDTO requestGuestDTO = RequestGuestDTO.builder()
                .name("Глеб")
                .surname("Глебович")
                .age(28)
                .passport("AС1234569")
                .phone("1888888888")
                .day(1)
                .build();

        Guest guest = requestGuestMapper.toEntity(requestGuestDTO);

        assertThat(guest.getName(), equalTo(requestGuestDTO.getName()));
        assertThat(guest.getSurname(), equalTo(requestGuestDTO.getSurname()));
        assertThat(guest.getAge(), equalTo(requestGuestDTO.getAge()));
        assertThat(guest.getPassport(), equalTo(requestGuestDTO.getPassport()));
        assertThat(guest.getPhone(), equalTo(requestGuestDTO.getPhone()));
    }
}
