package com.java.hotel.service.dto.service;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestPurchaseServiceDTO {

    @NotNull
    @Positive
    private Long guestId;

    @NotNull
    @Positive
    private Long serviceId;
}
