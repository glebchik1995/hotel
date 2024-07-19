package com.java.hotel.controller.user.guest;

import com.java.hotel.service.IGuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static constant.RequestPrefix.API_PREFIX_FOR_USER_GUESTS;

@RestController
@RequestMapping(path = API_PREFIX_FOR_USER_GUESTS)
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Гости - Пользователь", description = "Возможность совершать различные действия с информацией о гостях в роле пользователя")
public class UGuestController {

    private final IGuestService guestService;

    @GetMapping("/check-in/count")
    @Operation(summary = "Получить количество заселенных клиентов")
    public ResponseEntity<Long> getCountOfCheckedInGuest() {
        long count = guestService.getCountCheckedInGuest();
        return ResponseEntity.ok(count);
    }
}

