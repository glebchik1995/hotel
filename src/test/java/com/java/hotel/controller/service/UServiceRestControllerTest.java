package com.java.hotel.controller.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.java.hotel.config.BaseIntegrationTest;
import com.java.hotel.dao.ServiceDAO;
import com.java.hotel.service.IServiceService;
import com.java.hotel.service.dto.service.ServiceDTO;
import com.java.hotel.service.filter.CriteriaModel;
import com.java.hotel.service.filter.GenericSpecification;
import com.java.hotel.service.filter.Operation;
import com.java.hotel.service.mapper.service.PurchaseServiceMapper;
import com.java.hotel.service.mapper.service.ServiceMapper;
import com.java.hotel.service.model.service.Service;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WithMockUser(username = "user", authorities = {"USER"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class UServiceRestControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mvc;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    private IServiceService serviceService;

    @Autowired
    private ServiceMapper serviceMapper;

    @Autowired
    private PurchaseServiceMapper purchaseMapper;

    @Autowired
    private ServiceDAO serviceDAO;

    @Test
    @DisplayName("Получить услугу по ID")
    void shouldGetServiceById() throws Exception {
        long serviceId = 1L;

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/user/services/" + serviceId))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        response.setCharacterEncoding("UTF-8");

        Service service = serviceDAO.findById(serviceId).orElse(null);

        ServiceDTO serviceDTO = serviceMapper.toDto(service);

        Assertions.assertEquals(mapper.writeValueAsString(serviceDTO), response.getContentAsString());

    }

    @Test
    @DisplayName("Получить все услуги")
    void shouldGetServices() throws Exception {

        CriteriaModel criteriaModel = new CriteriaModel("price", Operation.EQ, 250);
        Specification<Service> specification = new GenericSpecification<>(criteriaModel, Service.class);
        Pageable pageable = PageRequest.of(0, 15, Sort.by("id").ascending());

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/user/services")
                        .param("criteriaJson", mapper.writeValueAsString(criteriaModel))
                        .param("page", "0")
                        .param("size", "15")
                        .param("sort", "id"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        response.setCharacterEncoding("UTF-8");

        Page<Service> expiredPage = serviceDAO.findAll(specification, pageable);
        Page<ServiceDTO> dtoPage = expiredPage.map(serviceMapper::toDto);
        assertEquals(mapper.writeValueAsString(dtoPage), response.getContentAsString());
    }

    @Test
    @DisplayName("Получить количество доступных услуг")
    void shouldGetCountAvailableService() throws Exception {

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/user/services/count/available"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        response.setCharacterEncoding("UTF-8");

        long count = serviceDAO.countServicesByAvailableTrue();

        Assertions.assertEquals(count, Long.parseLong(response.getContentAsString()));

    }
}
