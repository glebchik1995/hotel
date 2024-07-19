package com.java.hotel.service.dto.room;

import com.java.hotel.service.model.room.State;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomDTO {

    @PositiveOrZero
    private Long id;

    @NotNull(message = "Состояние не должно быть нулевым")
    private State state;

    @Min(value = 1, message = "Размер должен быть не менее 1")
    @Max(value = 3, message = "Размер должен быть не более 3")
    @NotNull(message = "Размерность гостиничного номера обязательна")
    @Schema(description = "Размер гостиницы", example = "3")
    private int size;

    @PositiveOrZero(message = "Цена должна быть положительным числом или нулем")
    @NotNull(message = "Цена обязательна")
    @Schema(description = "Цена гостиничного номера в сутки", example = "3333")
    private int price;

    @Min(value = 1, message = "Звезда должна быть не менее 1")
    @Max(value = 5, message = "Звезда должна быть не более 5")
    @NotNull(message = "Количество звезд обязательно")
    @Schema(description = "Количество звезд гостиничного номера", example = "5")
    private int star;
}
