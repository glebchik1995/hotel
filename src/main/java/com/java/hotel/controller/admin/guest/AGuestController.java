package com.java.hotel.controller.admin.guest;

import com.java.hotel.service.IGuestService;
import com.java.hotel.service.dto.guest.GuestAndRoomDTO;
import com.java.hotel.service.dto.guest.GuestCheckInDTO;
import com.java.hotel.service.dto.guest.GuestDTO;
import com.java.hotel.service.dto.guest.RequestGuestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static constant.RequestPrefix.API_PREFIX_FOR_ADMIN_GUESTS;

@RestController
@RequestMapping(path = API_PREFIX_FOR_ADMIN_GUESTS)
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Гости - Администратор", description = "Возможность совершать различные действия с информацией о гостях в роле администратора")
public class AGuestController {

    private final IGuestService guestService;

    @PostMapping("/check-in")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Заселение гост(я)/(ей) в номер")
    public List<GuestCheckInDTO> checkIn(@NotEmpty(message = "Список гостей не может быть пустым")
                                         @Size(min = 1, max = 3, message = "Количество гостей должно быть от 1 до 3")
                                         @RequestBody List<@Valid RequestGuestDTO> guestDTOS) {
        return guestService.checkIn(guestDTOS);
    }

    @GetMapping("/{guestId}")
    @Operation(summary = "Получения гостя по ID")
    public GuestDTO getGuestById(@PathVariable Long guestId) {
        return guestService.findById(guestId);
    }

    @GetMapping
    @Operation(summary = "Получение всех заселившихся гостей")
    public Page<GuestCheckInDTO> getAllCheckedInGuests(@ParameterObject Pageable pageable) {
        return guestService.getAllGuests(pageable);
    }

    @GetMapping("/history/{roomId}")
    @Operation(summary = "Получение истории гостей по ID гостиничного номера")
    public Page<GuestDTO> getHistoryGuestsByRoomId(@PathVariable Long roomId, @ParameterObject Pageable pageable) {
        return guestService.getHistoryGuestsByRoomId(roomId, pageable);
    }

    @GetMapping("/history")
    @Operation(summary = "Получение истории гостей во всех номерах")
    public Page<GuestDTO> getHistoryGuestsInAllRooms(@ParameterObject Pageable pageable) {
        return guestService.getHistoryGuestsForAllTime(pageable);
    }

    @GetMapping("/and_rooms")
    @Operation(summary = "Получение гостей и их номеров")
    public Page<GuestAndRoomDTO> getGuestsAndTheirRooms(@ParameterObject Pageable pageable) {
        return guestService.getGuestsAndTheirRooms(pageable);
    }

    @GetMapping("/last-3")
    @Operation(summary = "Получение последних трех заселившихся гостей")
    public List<GuestCheckInDTO> getLast3GuestsToCheckIn() {
        return guestService.getLast3GuestsToCheckIn();
    }

    @DeleteMapping("/{roomId}")
    @Operation(summary = "Выселение гостей по ID гостиничного номера")
    public void checkOut(@PathVariable Long roomId) {
        guestService.checkOut(roomId);
    }
}