package com.java.hotel.controller.user.room;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.hotel.dao.RoomDAO;
import com.java.hotel.service.IRoomService;
import com.java.hotel.service.dto.room.RoomDTO;
import com.java.hotel.service.filter.CriteriaModel;
import com.java.hotel.service.mapper.room.RoomMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;

import static constant.RequestPrefix.API_PREFIX_FOR_USER_ROOMS;

@RestController
@RequestMapping(path = API_PREFIX_FOR_USER_ROOMS)
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Гостиница - Пользователь", description = "Возможность совершать различные действия с номерами в роле пользователя")
public class URoomController {

    private final IRoomService roomService;

    private final RoomDAO roomDAO;

    private final RoomMapper roomMapper;

    @GetMapping("/{roomId}")
    @Operation(summary = "Получение гостиничного номера по ID")
    public RoomDTO getRoomById(@PathVariable Long roomId) {
        return roomService.getRoomById(roomId);
    }

    @GetMapping
    @Operation(summary = "Получение всех номеров в гостинице")
    public Page<RoomDTO> getRooms(@RequestParam(required = false) String criteriaJson,
                                  @ParameterObject Pageable pageable) throws BadRequestException {
        CriteriaModel model = null;
        if (criteriaJson != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                model = objectMapper.readValue(criteriaJson, CriteriaModel.class);
            } catch (IOException ex) {
                throw new BadRequestException("Не удалось проанализировать условия", ex);
            }
        }
        if (model != null) {
            return roomService.getRooms(model, pageable);
        } else {
            return roomDAO.findAll(pageable).map(roomMapper::toDto);
        }
    }


    @GetMapping("/available")
    @Operation(summary = "Получение гостиничных номеров на дату")
    public Page<RoomDTO> getRoomsOnDate(@RequestParam(name = "date") LocalDate date,
                                        @ParameterObject Pageable pageable) {
        return roomService.getRoomsOnDate(date, pageable);
    }

    @GetMapping("/free/count")
    @Operation(summary = "Получение количества свободных номеров")
    public long getCountFreeRooms() {
        return roomService.getCountFreeRooms();
    }
}
