package com.java.hotel.service.dto.service;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestUpdateServicePriceDTO {

    @NotNull
    @Positive
    private Long serviceId;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Новая цена услуги", example = "666")
    private int price;
}
