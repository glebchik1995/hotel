package com.java.hotel.service.dto.room;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestRoomDateDTO {

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Неправильный формат даты. Используйте формат yyyy-MM-dd.")
    @Future(message = "Дата должна быть в будущем относительно текущей даты.")
    @Schema(description = "Поиск свободной даты", example = "2024-04-21")
    private LocalDate date;
}
