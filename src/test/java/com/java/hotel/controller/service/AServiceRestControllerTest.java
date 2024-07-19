package com.java.hotel.controller.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.java.hotel.config.BaseIntegrationTest;
import com.java.hotel.dao.GuestDAO;
import com.java.hotel.dao.PurchaseDAO;
import com.java.hotel.dao.ServiceDAO;
import com.java.hotel.service.IServiceService;
import com.java.hotel.service.dto.service.*;
import com.java.hotel.service.mapper.service.PurchaseServiceMapper;
import com.java.hotel.service.mapper.service.ServiceMapper;
import com.java.hotel.service.model.guest.Guest;
import com.java.hotel.service.model.purchase.Purchase;
import com.java.hotel.service.model.service.Category;
import com.java.hotel.service.model.service.Service;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@WithMockUser(username = "admin", authorities = {"ADMIN"})
class AServiceRestControllerTest extends BaseIntegrationTest {

    @Autowired
    private IServiceService serviceService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ServiceMapper serviceMapper;

    @Autowired
    private PurchaseServiceMapper purchaseMapper;

    @Autowired
    private ServiceDAO serviceDAO;

    @Autowired
    private PurchaseDAO purchaseDAO;

    @Autowired
    private GuestDAO guestDAO;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    @DisplayName("Добавить услугу в гостиницу")
    void shouldAddService() throws Exception {

        Service service = Service.builder()
                .id(5L)
                .title("Завтрак")
                .price(100)
                .category(Category.FOOD)
                .available(true)
                .build();

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.post("/admin/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(service))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        response.setCharacterEncoding("UTF-8");

        Service savedService = serviceDAO.save(service);

        ServiceDTO serviceDTO = serviceMapper.toDto(savedService);

        Assertions.assertEquals(mapper.writeValueAsString(serviceDTO), response.getContentAsString());

    }

    @Test
    @DisplayName("Приобрести услуги")
    void shouldPurchaseService() throws Exception {

        LocalDate date = LocalDate.now();

        RequestPurchaseServiceDTO requestPurchaseServiceDTO = RequestPurchaseServiceDTO.builder()
                .serviceId(1L)
                .guestId(1L)
                .build();

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.post("/admin/services/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestPurchaseServiceDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        Guest guest = guestDAO.findById(requestPurchaseServiceDTO.getGuestId()).orElse(null);

        Service service = serviceDAO.findById(requestPurchaseServiceDTO.getServiceId()).orElse(null);

        Assertions.assertNotNull(guest);
        Assertions.assertNotNull(service);

        Purchase purchaseService = purchaseDAO.save(new Purchase(guest, service, date, date.plusDays(1)));

        PurchaseServiceDTO purchaseServiceDTO = purchaseMapper.toDto(purchaseService);

        Assertions.assertEquals(mapper.writeValueAsString(purchaseServiceDTO), response.getContentAsString());
    }

    @Test
    @DisplayName("Изменить цену на услугу")
    void shouldUpdateServicePrice() throws Exception {

        RequestUpdateServicePriceDTO updateServicePriceDTO = RequestUpdateServicePriceDTO.builder()
                .serviceId(1L)
                .price(200)
                .build();

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.put("/admin/services/price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateServicePriceDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        Service service = serviceDAO.findById(updateServicePriceDTO.getServiceId()).orElse(null);
        Assertions.assertNotNull(service);
        service.setPrice(updateServicePriceDTO.getPrice());
        Service updateService = serviceDAO.save(service);
        ServiceDTO serviceDTO = serviceMapper.toDto(updateService);
        Assertions.assertEquals(mapper.writeValueAsString(serviceDTO), response.getContentAsString());
    }

    @Test
    @DisplayName("Удалить услугу")
    void shouldDeleteService() throws Exception {
        Long serviceId = 1L;
        mvc.perform(MockMvcRequestBuilders.delete("/admin/services/" + serviceId))
                .andExpect(status().isOk());
        Service service = serviceDAO.findById(serviceId).orElse(null);
        Assertions.assertNull(service);


    }

    @Test
    @DisplayName("Получайте купленные услуги по гостевому идентификатору")
    void shouldGetPurchasedServicesByGuestId() throws Exception {

        Long guestId = 1L;

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/admin/services/" + guestId))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        Page<Purchase> purchase = purchaseDAO.findPurchaseByGuest_IdOrderBy(guestId, PageRequest.of(0, 20));

        Page<PurchaseServiceSimpleDTO> dtoPage = purchase.map(purchaseMapper::toSimpleDto);

        Assertions.assertEquals(mapper.writeValueAsString(dtoPage), response.getContentAsString());

    }
}
