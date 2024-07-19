package com.java.hotel.controller.room;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.java.hotel.config.BaseIntegrationTest;
import com.java.hotel.dao.RoomDAO;
import com.java.hotel.service.IRoomService;
import com.java.hotel.service.dto.room.RoomDTO;
import com.java.hotel.service.filter.CriteriaModel;
import com.java.hotel.service.filter.GenericSpecification;
import com.java.hotel.service.filter.Operation;
import com.java.hotel.service.mapper.room.RoomMapper;
import com.java.hotel.service.model.room.Room;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WithMockUser(username = "user", authorities = {"USER"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class URoomRestControllerTest extends BaseIntegrationTest {

    @Autowired
    private IRoomService roomService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private RoomDAO roomDAO;

    @Autowired
    private RoomMapper roomMapper;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    @DisplayName("getRoomById")
    void shouldAcceptRequestRoomByID_ReturnsRoomDTO() throws Exception {

        MockHttpServletResponse response = mvc.perform(get("/user/rooms/1"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        response.setCharacterEncoding("UTF-8");

        Room room = roomDAO.findById(1L).orElse(null);

        Assertions.assertNotNull(room);

        RoomDTO roomDTO = roomMapper.toDto(room);

        assertEquals(mapper.writeValueAsString(roomDTO), response.getContentAsString());


    }

    @Test
    @DisplayName("getRooms без критериев")
    void shouldAcceptRequestRoomsDTO_ReturnsPageRoomDTO() throws Exception {

        Pageable pageable = PageRequest.of(0, 10);

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/user/rooms")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        response.setCharacterEncoding("UTF-8");

        Page<Room> expiredPage = roomDAO.findAll(pageable);

        Page<RoomDTO> dtoPage = expiredPage.map(roomMapper::toDto);

        assertEquals(mapper.writeValueAsString(dtoPage), response.getContentAsString());
    }

    @Test
    @DisplayName("getRooms с критериями")
    void shouldAcceptRequestRoomsDTO_ReturnsPageRoomDTOByCriteria() throws Exception {

        CriteriaModel criteriaModel = new CriteriaModel("star", Operation.EQ, 4);
        Specification<Room> specification = new GenericSpecification<>(criteriaModel, Room.class);
        Pageable pageable = PageRequest.of(0, 15, Sort.by("star").ascending());

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/user/rooms")
                        .param("criteriaJson", mapper.writeValueAsString(criteriaModel))
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize()))
                        .param("sort", "star"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        Page<Room> expiredPage = roomDAO.findAll(specification, pageable);
        Page<RoomDTO> dtoPage = expiredPage.map(roomMapper::toDto);
        assertEquals(mapper.writeValueAsString(dtoPage), response.getContentAsString());
    }

    @Test
    @DisplayName("getRoomsOnDate")
    void shouldAcceptRequestRoomsOnDate_PageReturnsRoomDTO() throws Exception {

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/user/rooms/available")
                        .param("page", "0")
                        .param("size", "10")
                        .param("date", "2024-12-01"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        response.setCharacterEncoding("UTF-8");

        Page<RoomDTO> page = roomService.getRoomsOnDate(LocalDate.parse("2024-12-01"), PageRequest.of(0, 10));

        Assertions.assertEquals(mapper.writeValueAsString(page), response.getContentAsString());
    }

    @Test
    @DisplayName("getCountFreeRooms")
    void shouldReturnsCountFreeRoomsToLongType() throws Exception {

        MockHttpServletResponse response = mvc.perform(get("/user/rooms/free/count"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        response.setCharacterEncoding("UTF-8");

        long expectedValue = roomDAO.countRoomsByState_Free();

        assertEquals(expectedValue, Long.parseLong(response.getContentAsString()));
    }
}
