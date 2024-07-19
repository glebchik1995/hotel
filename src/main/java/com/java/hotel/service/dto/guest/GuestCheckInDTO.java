package com.java.hotel.service.dto.guest;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GuestCheckInDTO {

    private GuestDTO guest;

    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Дата заселения обязательна")
    @Schema(description = "Дата заселения", example = "2024-04-21")
    private LocalDate dateCheckIn;

    @NotNull(message = "Дата выселения обязательна")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @FutureOrPresent
    @Schema(description = "Дата выселения", example = "2024-04-22")
    private LocalDate dateCheckOut;
}
