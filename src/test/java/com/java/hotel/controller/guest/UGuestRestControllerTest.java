package com.java.hotel.controller.guest;

import com.java.hotel.config.BaseIntegrationTest;
import com.java.hotel.dao.ResidenceDAO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@WithMockUser(username = "user", authorities = {"USER"})
class UGuestRestControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ResidenceDAO residenceDAO;

    @Test
    @DisplayName("Get count of checked-in guests")
    void shouldReturnCountOfCheckedInGuest() throws Exception {

        MockHttpServletResponse response = mvc.perform(get("/user/guests/check-in/count"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        Long expectedCount = residenceDAO.countResidence();

        assertEquals(expectedCount, Long.parseLong(response.getContentAsString()));
    }
}
