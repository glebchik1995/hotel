package com.java.hotel.controller.guest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.java.hotel.config.BaseIntegrationTest;
import com.java.hotel.dao.GuestDAO;
import com.java.hotel.dao.ResidenceDAO;
import com.java.hotel.dao.RoomDAO;
import com.java.hotel.dao.exception.DataNotFoundException;
import com.java.hotel.service.IGuestService;
import com.java.hotel.service.IRoomService;
import com.java.hotel.service.dto.guest.GuestAndRoomDTO;
import com.java.hotel.service.dto.guest.GuestCheckInDTO;
import com.java.hotel.service.dto.guest.GuestDTO;
import com.java.hotel.service.dto.guest.RequestGuestDTO;
import com.java.hotel.service.mapper.guest.GuestAndRoomMapper;
import com.java.hotel.service.mapper.guest.GuestCheckInMapper;
import com.java.hotel.service.mapper.guest.GuestMapper;
import com.java.hotel.service.model.guest.Guest;
import com.java.hotel.service.model.residence.Residence;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@WithMockUser(username = "admin", authorities = {"ADMIN"})
class AGuestRestControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mvc;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    private GuestMapper guestMapper;

    @Autowired
    private GuestAndRoomMapper guestAndRoomMapper;

    @Autowired
    private GuestCheckInMapper guestCheckInMapper;

    @Autowired
    private IGuestService guestService;

    @Autowired
    private IRoomService roomService;

    @Autowired
    private GuestDAO guestDAO;

    @Autowired
    private RoomDAO roomDAO;

    @Autowired
    private ResidenceDAO residenceDAO;

    @Test
    @DisplayName("Заселение гостей")
    void testCheckIn() throws Exception {

        List<RequestGuestDTO> guestDTOList = List.of(
                new RequestGuestDTO("Иван",
                        "Иванов",
                        30,
                        "AB1234567",
                        "1234567890",
                        5),

                new RequestGuestDTO("Александр",
                        "Александрович",
                        40,
                        "AA1234567",
                        "1234567880",
                        5),

                new RequestGuestDTO("Глеб",
                        "Глебович",
                        28,
                        "AA1234569",
                        "1888888888",
                        5)
        );

        MockHttpServletResponse response = mvc.perform(post("/admin/guests/check-in")
                        .content(mapper.writeValueAsString(guestDTOList))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        response.setCharacterEncoding("UTF-8");

        List<GuestCheckInDTO> guestCheckInDTOS = guestService.checkIn(guestDTOList);

        Assertions.assertEquals(mapper.writeValueAsString(guestCheckInDTOS), response.getContentAsString());
    }

    @Test
    @DisplayName("Получение заселившегося гостя по GUEST_ID")
    void testGetGuestById() throws Exception {

        Long guestId = 1L;

        MockHttpServletResponse response = mvc.perform(get("/admin/guests/" + guestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        Guest guest = guestDAO.findById(guestId).orElseThrow(() ->
                new DataNotFoundException(String.format("Гость с идентификатором %d не найден", guestId)));

        GuestDTO guestDTO = guestMapper.toDto(guest);

        Assertions.assertEquals(mapper.writeValueAsString(guestDTO), response.getContentAsString());

    }

    @Test
    @DisplayName("Getting all checked-in guests")
    void testGetAllCheckedInGuests() throws Exception {

        MockHttpServletResponse response = mvc.perform(get("/admin/guests")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        response.setCharacterEncoding("UTF-8");

        Page<Residence> residencePage = residenceDAO.findAll(PageRequest.of(0, 10));

        Page<GuestCheckInDTO> dtoPage = residencePage.map(guestCheckInMapper::toDto);

        Assertions.assertEquals(mapper.writeValueAsString(dtoPage), response.getContentAsString());
    }

    @Test
    @DisplayName("Получение истории гостей по ROOM_ID")
    void testGetHistoryGuestsByRoomId() throws Exception {

        Long roomId = 1L;

        MockHttpServletResponse response = mvc.perform(get("/admin/guests/history/" + roomId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        response.setCharacterEncoding("UTF-8");

        Page<Guest> page = roomDAO.findGuestHistoryByRoomId(roomId, PageRequest.of(0, 10));

        Page<GuestDTO> dtoPage = page.map(guestMapper::toDto);

        Assertions.assertEquals(mapper.writeValueAsString(dtoPage), response.getContentAsString());

    }

    @Test
    @DisplayName("Получение истории гостей во всех номерах")
    void testGetHistoryGuestsForAllTime() throws Exception {

        MockHttpServletResponse response = mvc.perform(get("/admin/guests/history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        response.setCharacterEncoding("UTF-8");

        Page<Guest> page = roomDAO.findAllGuestHistory(PageRequest.of(0, 10));

        Page<GuestDTO> dtoPage = page.map(guestMapper::toDto);

        Assertions.assertEquals(mapper.writeValueAsString(dtoPage), response.getContentAsString());
    }

    @Test
    @DisplayName("Получение гостей и их комнат")
    void testGetGuestsAndTheirRooms() throws Exception {

        MockHttpServletResponse response = mvc.perform(get("/admin/guests/and_rooms")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        response.setCharacterEncoding("UTF-8");

        Page<Residence> page = residenceDAO.findAll(PageRequest.of(0, 10));

        Page<GuestAndRoomDTO> dtoPage = page.map(guestAndRoomMapper::toDto);

        Assertions.assertEquals(mapper.writeValueAsString(dtoPage), response.getContentAsString());
    }

    @Test
    @DisplayName("Получение последних 3 заселившихся гостей")
    void testGetLast3GuestsToCheckIn() throws Exception {

        RequestGuestDTO guestDTO1 = RequestGuestDTO.builder()
                .name("Иван")
                .surname("Иванов")
                .age(30)
                .passport("AА1234567")
                .phone("1234567890")
                .day(1)
                .build();

        RequestGuestDTO guestDTO2 = RequestGuestDTO.builder()
                .name("Глеб")
                .surname("Глебович")
                .age(28)
                .passport("AA1234569")
                .phone("1888888888")
                .day(1)
                .build();

        List<RequestGuestDTO> guestDTOS = List.of(guestDTO1, guestDTO2);

        guestService.checkIn(guestDTOS);

        MockHttpServletResponse response = mvc.perform(get("/admin/guests/last-3")
                        .content(mapper.writeValueAsString(guestDTOS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        response.setCharacterEncoding("UTF-8");

        List<Residence> residencePage = residenceDAO.findAll().stream()
                .limit(3)
                .toList();

        List<GuestCheckInDTO> dtoPage = residencePage.stream().map(guestCheckInMapper::toDto).toList();

        Assertions.assertEquals(mapper.writeValueAsString(dtoPage), response.getContentAsString());
    }
}