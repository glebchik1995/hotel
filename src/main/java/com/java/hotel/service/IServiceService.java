package com.java.hotel.service;

import com.java.hotel.service.dto.service.*;
import com.java.hotel.service.filter.CriteriaModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IServiceService {

    ServiceDTO addService(ServiceDTO serviceDTO);

    ServiceDTO findServiceById(Long serviceId);

    Page<ServiceDTO> getServices(CriteriaModel criteriaList, Pageable pageable);

    void deleteService(Long id);

    ServiceDTO updatePriceService(RequestUpdateServicePriceDTO dto);

    long getCountAvailableService();

    Page<PurchaseServiceSimpleDTO> getPurchasedServicesByGuestId(Long guestId, Pageable page);

    PurchaseServiceDTO purchaseService(RequestPurchaseServiceDTO dto);

}
