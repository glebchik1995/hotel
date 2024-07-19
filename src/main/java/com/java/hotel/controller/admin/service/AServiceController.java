package com.java.hotel.controller.admin.service;

import com.java.hotel.service.IServiceService;
import com.java.hotel.service.dto.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static constant.RequestPrefix.API_PREFIX_FOR_ADMIN_SERVICES;

@RestController
@RequestMapping(path = API_PREFIX_FOR_ADMIN_SERVICES)
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Услуги - Администратор", description = "Возможность совершать различные действия с услугами в роле администратора")
public class AServiceController {

    private final IServiceService serviceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавление новой услуги в гостиницу")
    public ServiceDTO addService(@Valid @RequestBody ServiceDTO serviceDTO) {
        return serviceService.addService(serviceDTO);
    }

    @PostMapping("/purchase")
    @Operation(summary = "Приобрести услугу")
    public PurchaseServiceDTO purchaseService(@Valid @RequestBody RequestPurchaseServiceDTO dto) {
        return serviceService.purchaseService(dto);
    }

    @GetMapping("/{guestId}")
    @Operation(summary = "Добавление нового номера")
    public Page<PurchaseServiceSimpleDTO> getPurchasedServicesByGuestIdOrderBy(@PathVariable Long guestId,
                                                                               @ParameterObject Pageable pageable) {
        return serviceService.getPurchasedServicesByGuestId(guestId, pageable);
    }

    @PutMapping("/price")
    @Operation(summary = "Добавление нового номера")
    public ServiceDTO updateServicePrice(@Valid @RequestBody RequestUpdateServicePriceDTO dto) {
        return serviceService.updatePriceService(dto);
    }

    @DeleteMapping("/{serviceId}")
    @Operation(summary = "Удалить услугу из гостиницы")
    public void deleteService(@PathVariable Long serviceId) {
        serviceService.deleteService(serviceId);
    }

}
