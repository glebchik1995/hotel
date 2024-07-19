package com.java.hotel.controller.admin.room;

import com.java.hotel.service.IRoomService;
import com.java.hotel.service.dto.room.RequestUpdateRoomPriceDTO;
import com.java.hotel.service.dto.room.RoomDTO;
import com.java.hotel.service.impl.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static constant.RequestPrefix.API_PREFIX_FOR_ADMIN_ROOMS;

@RestController
@RequestMapping(path = API_PREFIX_FOR_ADMIN_ROOMS)
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Гостиница - Администратор", description = "Возможность совершать различные действия с номерами в роле администратора")
public class ARoomController {

    private final IRoomService roomService;

    @Autowired
    public ARoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    @Operation(summary = "Добавление нового номера")
    @ResponseStatus(HttpStatus.CREATED)
    public RoomDTO addRoom(@Valid @RequestBody RoomDTO roomDTO) {
        return roomService.addRoom(roomDTO);
    }

    @GetMapping("/full-price/{roomId}")
    @Operation(summary = "Получение всех номеров")
    public int getTotalCostForRoom(@PathVariable Long roomId) {
        return roomService.getTotalCostForRoom(roomId);
    }

    @PutMapping("/price")
    @Operation(summary = "Обновление цены гостиничного номера")
    public RoomDTO updateRoomPrice(@Valid @RequestBody RequestUpdateRoomPriceDTO dto) {
        return roomService.updatePriceRoom(dto);
    }

    @PutMapping("/close-for-repairs/{roomId}")
    @Operation(summary = "Закрытие номера на ремонт")
    public RoomDTO closeRoomForRepairs(@PathVariable Long roomId) {
        return roomService.closeRoomForRepairs(roomId);
    }

    @PutMapping("/open-after-repairs/{roomId}")
    @Operation(summary = "Открытие номера после ремонта")
    public RoomDTO openRoomAfterRepairs(@PathVariable Long roomId) {
        return roomService.openRoomAfterRepairs(roomId);
    }

    @DeleteMapping("/{roomId}")
    @Operation(summary = "Удаление номера")
    public void deleteRoom(@PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
    }

}