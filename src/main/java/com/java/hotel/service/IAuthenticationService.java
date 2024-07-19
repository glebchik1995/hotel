package com.java.hotel.service;

import com.java.hotel.service.dto.user.JwtAuthenticationResponse;
import com.java.hotel.service.dto.user.RefreshTokenRequest;
import com.java.hotel.service.dto.user.SignInRequest;
import com.java.hotel.service.dto.user.SignUpRequest;

public interface IAuthenticationService {

    JwtAuthenticationResponse signUp(SignUpRequest request);

    JwtAuthenticationResponse signIn(SignInRequest request);

    JwtAuthenticationResponse refresh(RefreshTokenRequest refreshTokenRequest);
}
