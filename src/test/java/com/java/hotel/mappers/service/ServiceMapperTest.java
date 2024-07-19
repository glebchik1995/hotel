package com.java.hotel.mappers.service;

import com.java.hotel.service.dto.service.ServiceDTO;
import com.java.hotel.service.mapper.service.ServiceMapper;
import com.java.hotel.service.model.service.Category;
import com.java.hotel.service.model.service.Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import com.java.hotel.config.BaseIntegrationTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@AutoConfigureMockMvc
class ServiceMapperTest extends BaseIntegrationTest {

    @Autowired
    private ServiceMapper serviceMapper;

    @Test
    void testToDto() {

        Service service = Service.builder()
                .title("Завтрак")
                .price(100)
                .category(Category.FOOD)
                .build();

        ServiceDTO serviceDTO = serviceMapper.toDto(service);

        assertThat(serviceDTO.getTitle(), equalTo(service.getTitle()));
        assertThat(serviceDTO.getPrice(), equalTo(service.getPrice()));
        assertThat(serviceDTO.getCategory(), equalTo(service.getCategory()));
    }

    @Test
    void toEntity() {

        ServiceDTO serviceDTO = ServiceDTO.builder()
                .title("Завтрак")
                .price(100)
                .category(Category.FOOD)
                .build();

        Service service = serviceMapper.toEntity(serviceDTO);

        assertThat(service.getTitle(), equalTo(serviceDTO.getTitle()));
        assertThat(service.getPrice(), equalTo(serviceDTO.getPrice()));
        assertThat(service.getCategory(), equalTo(serviceDTO.getCategory()));
    }
}
