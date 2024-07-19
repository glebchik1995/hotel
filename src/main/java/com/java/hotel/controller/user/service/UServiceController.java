package com.java.hotel.controller.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.hotel.dao.ServiceDAO;
import com.java.hotel.service.IServiceService;
import com.java.hotel.service.dto.service.ServiceDTO;
import com.java.hotel.service.filter.CriteriaModel;
import com.java.hotel.service.mapper.service.ServiceMapper;
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

import static constant.RequestPrefix.API_PREFIX_FOR_USER_SERVICES;

@RestController
@RequestMapping(path = API_PREFIX_FOR_USER_SERVICES)
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Услуги - Пользователь", description = "Возможность совершать различные действия с услугами в роле пользователя")
public class UServiceController {

    private final IServiceService serviceService;

    private final ServiceMapper serviceMapper;

    private final ServiceDAO serviceDAO;

    @GetMapping("/{serviceId}")
    @Operation(summary = "Получение услуги по ID")
    public ServiceDTO getServiceById(@PathVariable Long serviceId) {
        return serviceService.findServiceById(serviceId);
    }

    @GetMapping
    @Operation(summary = "Получение всех услуги в гостинице")
    public Page<ServiceDTO> getServices(@RequestParam(required = false) String criteriaJson,
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
            return serviceService.getServices(model, pageable);
        } else {
            return serviceDAO.findAll(pageable).map(serviceMapper::toDto);
        }
    }

    @GetMapping("/count/available")
    @Operation(summary = "Получение количества свободных доступных для покупки услуг")
    public Long getCountAvailableService() {
        return serviceService.getCountAvailableService();
    }
}
