package com.java.hotel.service.dto.service;


import com.java.hotel.service.model.service.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
public class ServiceDTO {

    @PositiveOrZero
    private Long id;

    @NotBlank(message = "Название не должен быть пустым")
    private String title;

    @PositiveOrZero(message = "Цена должна быть положительным числом или нулем")
    @NotNull(message = "Цена обязательна")
    @Schema(description = "Цена услуги", example = "333")
    private int price;

    @NotNull(message = "Категория не должна быть пустой")
    @Schema(description = "Категория услуги", example = "FOOD")
    private Category category;

}
