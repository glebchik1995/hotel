package com.java.hotel.service.dto.guest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestGuestDTO {

    @NotBlank(message = "Имя обязательно")
    @Schema(description = "Имя гостя", example = "Иван")
    private String name;

    @NotBlank(message = "Фамилия обязательна")
    @Schema(description = "Фамилия гостя", example = "Иванов")
    private String surname;

    @Min(value = 18, message = "Возраст должен быть не менее 18 лет")
    @Max(value = 110, message = "Возраст должен быть не более 110 лет")
    @NotNull(message = "Возраст обязателен")
    @Schema(description = "Возраст гостя", example = "30")
    private int age;

    @Pattern(regexp = "[A-Z]{2}[0-9]{7}", message = "Паспорт должен быть в формате: AA1234567")
    @NotBlank(message = "Паспортные данные обязательны")
    @Schema(description = "Паспортные данные гостя", example = "AB1234567")
    private String passport;

    @Pattern(regexp = "\\d{10}", message = "Номер телефона должен состоять из 10 цифр")
    @NotBlank(message = "Номер телефона обязателен")
    @Schema(description = "Номер телефона гостя", example = "1234567890")
    private String phone;

    @Min(1)
    @NotNull(message = "Нельзя заселиться меньше чем на 1 день")
    @Schema(description = "Количество дней проживания", example = "5")
    private int day;
}
