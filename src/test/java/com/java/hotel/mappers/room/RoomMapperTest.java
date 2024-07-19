package com.java.hotel.mappers.room;


import com.java.hotel.service.dto.room.RoomDTO;
import com.java.hotel.service.mapper.room.RoomMapper;
import com.java.hotel.service.model.room.Room;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import com.java.hotel.config.BaseIntegrationTest;

import static com.java.hotel.service.model.room.State.FREE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@AutoConfigureMockMvc
class RoomMapperTest extends BaseIntegrationTest {

    @Autowired
    private RoomMapper roomMapper;

    @Test
    void testToDto() {

        Room room = Room.builder()
                .state(FREE)
                .size(2)
                .price(1000)
                .star(1)
                .build();

        RoomDTO roomDTO = roomMapper.toDto(room);


        assertThat(roomDTO.getState(), equalTo(room.getState()));
        assertThat(roomDTO.getSize(), equalTo(room.getSize()));
        assertThat(roomDTO.getPrice(), equalTo(room.getPrice()));
        assertThat(roomDTO.getStar(), equalTo(room.getStar()));
    }

    @Test
    void toEntity() {

        RoomDTO roomDTO = RoomDTO.builder()
                .state(FREE)
                .size(2)
                .price(1000)
                .star(1)
                .build();

        Room room = roomMapper.toEntity(roomDTO);

        assertThat(room.getState(), equalTo(roomDTO.getState()));
        assertThat(room.getSize(), equalTo(roomDTO.getSize()));
        assertThat(room.getPrice(), equalTo(roomDTO.getPrice()));
        assertThat(room.getStar(), equalTo(roomDTO.getStar()));
    }
}

