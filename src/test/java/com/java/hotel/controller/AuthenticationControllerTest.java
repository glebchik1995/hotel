package com.java.hotel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.hotel.config.BaseIntegrationTest;
import com.java.hotel.service.IAuthenticationService;
import com.java.hotel.service.dto.user.JwtAuthenticationResponse;
import com.java.hotel.service.dto.user.RefreshTokenRequest;
import com.java.hotel.service.dto.user.SignInRequest;
import com.java.hotel.service.dto.user.SignUpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WithMockUser(username = "user", authorities = {"USER"})
public class AuthenticationControllerTest extends BaseIntegrationTest {

    @Mock
    private MockMvc mvc;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private IAuthenticationService authenticationService;

    @BeforeEach
    void init() {
        mvc = MockMvcBuilders
                .standaloneSetup(authenticationController)
                .build();
    }

    @Test
    void testSignUp() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest("Jon", "jondoe@gmail.com", "my_1secret1_password");
        JwtAuthenticationResponse response = new JwtAuthenticationResponse("accessToken", "refreshToken");

        when(authenticationService.signUp(any(SignUpRequest.class))).thenReturn(response);

        mvc.perform(MockMvcRequestBuilders.post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signUpRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"));
    }

    @Test
    void testSignIn() throws Exception {
        SignInRequest signInRequest = new SignInRequest("username", "password");
        JwtAuthenticationResponse response = new JwtAuthenticationResponse("accessToken", "refreshToken");

        when(authenticationService.signIn(any(SignInRequest.class))).thenReturn(response);

        mvc.perform(MockMvcRequestBuilders.post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signInRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"));
    }

    @Test
    void testRefreshToken() throws Exception {
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest("refreshToken");
        JwtAuthenticationResponse response = new JwtAuthenticationResponse("accessToken", "refreshToken");

        when(authenticationService.refresh(any(RefreshTokenRequest.class))).thenReturn(response);

        mvc.perform(MockMvcRequestBuilders.post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(refreshTokenRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));

    }
}
