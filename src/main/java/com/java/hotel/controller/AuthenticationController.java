package com.java.hotel.controller;

import com.java.hotel.service.IAuthenticationService;
import com.java.hotel.service.dto.user.JwtAuthenticationResponse;
import com.java.hotel.service.dto.user.RefreshTokenRequest;
import com.java.hotel.service.dto.user.SignInRequest;
import com.java.hotel.service.dto.user.SignUpRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static constant.RequestPrefix.API_PREFIX_FOR_AUTHENTICATION;

@RestController
@RequestMapping(API_PREFIX_FOR_AUTHENTICATION)
@RequiredArgsConstructor
@Tag(name = "Аутентификация")
public class AuthenticationController {

    private final IAuthenticationService authenticationService;

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/sign-up")
    public ResponseEntity<JwtAuthenticationResponse> signUp(@RequestBody @Valid SignUpRequest request) {
        return ResponseEntity.ok(authenticationService.signUp(request));
    }

    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/sign-in")
    public ResponseEntity<JwtAuthenticationResponse> signIn(@RequestBody @Valid SignInRequest request) {
        return ResponseEntity.ok(authenticationService.signIn(request));

    }

    @Operation(summary = "Обновление токена")
    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        return ResponseEntity.ok(authenticationService.refresh(request));

    }
}