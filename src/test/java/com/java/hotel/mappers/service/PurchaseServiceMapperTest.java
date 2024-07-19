package com.java.hotel.mappers.service;

import com.java.hotel.service.dto.service.PurchaseServiceDTO;
import com.java.hotel.service.dto.service.PurchaseServiceSimpleDTO;
import com.java.hotel.service.mapper.service.PurchaseServiceMapper;
import com.java.hotel.service.model.guest.Guest;
import com.java.hotel.service.model.purchase.Purchase;
import com.java.hotel.service.model.service.Category;
import com.java.hotel.service.model.service.Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import com.java.hotel.config.BaseIntegrationTest;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@AutoConfigureMockMvc
class PurchaseServiceMapperTest extends BaseIntegrationTest {

    @Autowired
    private PurchaseServiceMapper purchaseServiceMapper;

    private Purchase purchase;

    @BeforeEach
    void init() {

        Guest guest = Guest.builder()
                .name("Глеб")
                .surname("Глебович")
                .age(28)
                .passport("AС1234569")
                .phone("1888888888")
                .build();

        Service service = Service.builder()
                .title("Завтрак")
                .price(100)
                .category(Category.FOOD)
                .build();

        purchase = Purchase.builder()
                .guest(guest)
                .service(service)
                .dateStartRental(LocalDate.parse("2024-04-22"))
                .dateEndRental(LocalDate.parse("2024-04-23"))
                .build();

    }

    @Test
    void testToSimpleDto() {

        PurchaseServiceSimpleDTO purchaseServiceSimpleDTO = purchaseServiceMapper.toSimpleDto(purchase);

        assertThat(purchaseServiceSimpleDTO.getGuestId(), equalTo(purchaseServiceSimpleDTO.getGuestId()));

        assertThat(purchaseServiceSimpleDTO.getService().getTitle(), equalTo(purchase.getService().getTitle()));
        assertThat(purchaseServiceSimpleDTO.getService().getPrice(), equalTo(purchase.getService().getPrice()));
        assertThat(purchaseServiceSimpleDTO.getService().getCategory(), equalTo(purchase.getService().getCategory()));

        assertThat(purchaseServiceSimpleDTO.getDateStartRental(), equalTo(purchase.getDateStartRental()));
        assertThat(purchaseServiceSimpleDTO.getDateEndRental(), equalTo(purchase.getDateEndRental()));
    }

    @Test
    void testToDto() {

        PurchaseServiceDTO purchaseServiceDTO = purchaseServiceMapper.toDto(purchase);

        assertThat(purchaseServiceDTO.getGuest().getName(), equalTo(purchase.getGuest().getName()));
        assertThat(purchaseServiceDTO.getGuest().getSurname(), equalTo(purchase.getGuest().getSurname()));
        assertThat(purchaseServiceDTO.getGuest().getAge(), equalTo(purchase.getGuest().getAge()));
        assertThat(purchaseServiceDTO.getGuest().getPassport(), equalTo(purchase.getGuest().getPassport()));
        assertThat(purchaseServiceDTO.getGuest().getPhone(), equalTo(purchase.getGuest().getPhone()));

        assertThat(purchaseServiceDTO.getService().getTitle(), equalTo(purchase.getService().getTitle()));
        assertThat(purchaseServiceDTO.getService().getPrice(), equalTo(purchase.getService().getPrice()));
        assertThat(purchaseServiceDTO.getService().getCategory(), equalTo(purchase.getService().getCategory()));

        assertThat(purchaseServiceDTO.getDateStartRental(), equalTo(purchase.getDateStartRental()));
        assertThat(purchaseServiceDTO.getDateEndRental(), equalTo(purchase.getDateEndRental()));
    }
}
