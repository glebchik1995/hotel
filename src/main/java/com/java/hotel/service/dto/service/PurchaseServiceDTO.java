package com.java.hotel.service.dto.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.java.hotel.service.dto.guest.GuestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseServiceDTO {

    private GuestDTO guest;

    private ServiceDTO service;

    @FutureOrPresent
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull(message = "Дата начала аренды обязательна")
    @Schema(description = "Дата взятия услуги в аренду", example = "2024-04-22")
    private LocalDate dateStartRental;

    @FutureOrPresent
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull(message = "Дата завершения аренды обязательна")
    @Schema(description = "Дата окончания аренду услуги", example = "2024-04-22")
    private LocalDate dateEndRental;
}
