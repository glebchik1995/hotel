package com.java.hotel.service.dto.room;

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
public class RequestUpdateRoomPriceDTO {

    @NotNull
    @Positive
    private Long roomId;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Новая цена гостиничного номера", example = "6666")
    private int price;
}
