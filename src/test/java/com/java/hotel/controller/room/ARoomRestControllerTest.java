package com.java.hotel.controller.room;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.java.hotel.config.BaseIntegrationTest;
import com.java.hotel.dao.PurchaseDAO;
import com.java.hotel.dao.RoomDAO;
import com.java.hotel.service.dto.room.RequestUpdateRoomPriceDTO;
import com.java.hotel.service.dto.room.RoomDTO;
import com.java.hotel.service.mapper.room.RoomMapper;
import com.java.hotel.service.model.room.Room;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;

import static com.java.hotel.service.model.room.State.BEING_REPAIRED;
import static com.java.hotel.service.model.room.State.FREE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WithMockUser(username = "admin", authorities = {"ADMIN"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class ARoomRestControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mvc;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    private RoomDAO roomDAO;

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private PurchaseDAO purchaseDAO;

    @Test
    @DisplayName("Добавление гостиничного номера")
    void testAddRoom() throws Exception {

        Room room = new Room(1L, FREE, 1, 2000, 3);

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.post("/admin/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(room))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        response.setCharacterEncoding("UTF-8");

        Room savedRoom = roomDAO.save(room);

        RoomDTO roomDTO = roomMapper.toDto(savedRoom);

        Assertions.assertEquals(mapper.writeValueAsString(roomDTO), response.getContentAsString());

    }

    @Test
    @DisplayName("Получить общую стоимость гостиничного номера")
    void testGetTotalCostForRoom() throws Exception {
        long roomId = 1L;

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/admin/rooms/full-price/" + roomId))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        response.setCharacterEncoding("UTF-8");

        long expectedCost = purchaseDAO.findTotalCostForByRoomId(roomId);

        assertEquals(expectedCost, Long.parseLong(response.getContentAsString()));

    }

    @Test
    @DisplayName("Изменить цену гостиничного номера")
    void testUpdateRoomPrice() throws Exception {

        RequestUpdateRoomPriceDTO updateRoomPriceDTO = new RequestUpdateRoomPriceDTO(1L, 2500);

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.put("/admin/rooms/price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateRoomPriceDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        response.setCharacterEncoding("UTF-8");

        Room room = roomDAO.findById(updateRoomPriceDTO.getRoomId()).orElse(null);

        assertNotNull(room);

        room.setPrice(updateRoomPriceDTO.getPrice());

        roomDAO.save(room);

        RoomDTO roomDTO = roomMapper.toDto(room);

        assertEquals(mapper.writeValueAsString(roomDTO), response.getContentAsString());

    }

    @Test
    @DisplayName("Закрыть гостиничный номер на ремонт")
    void testCloseRoomForRepairs() throws Exception {

        long roomId = 2L;

        Room room = roomDAO.findById(roomId).orElse(null);

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.put("/admin/rooms/close-for-repairs/" + roomId))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        response.setCharacterEncoding("UTF-8");

        assertNotNull(room);

        room.setState(BEING_REPAIRED);

        roomDAO.save(room);

        RoomDTO roomDTO = roomMapper.toDto(room);

        assertEquals(mapper.writeValueAsString(roomDTO), response.getContentAsString());
    }

    @Test
    @DisplayName("Открыть гостиничный номер после ремонта")
    void testOpenRoomAfterRepairs() throws Exception {

        long roomId = 2L;

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.put("/admin/rooms/open-after-repairs/" + roomId))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        response.setCharacterEncoding("UTF-8");

        Room room = roomDAO.findById(roomId).orElse(null);

        assertNotNull(room);

        room.setState(FREE);

        roomDAO.save(room);

        RoomDTO roomDTO = roomMapper.toDto(room);

        assertEquals(mapper.writeValueAsString(roomDTO), response.getContentAsString());
    }

    @Test
    @DisplayName("Удалить номер из гостиницы")
    void testDeleteRoom() throws Exception {

        long roomId = 1;

        mvc.perform(MockMvcRequestBuilders.delete("/admin/rooms/" + roomId))
                .andExpect(status().isOk());

        Room room = roomDAO.findById(roomId).orElse(null);

        Assertions.assertNull(room);

    }
}
