package com.java.hotel.service.impl;

import com.java.hotel.dao.GuestDAO;
import com.java.hotel.dao.PurchaseDAO;
import com.java.hotel.dao.ServiceDAO;
import com.java.hotel.dao.exception.DataAccessException;
import com.java.hotel.dao.exception.DataNotFoundException;
import com.java.hotel.service.IServiceService;
import com.java.hotel.service.dto.service.*;
import com.java.hotel.service.filter.CriteriaModel;
import com.java.hotel.service.filter.GenericSpecification;
import com.java.hotel.service.mapper.service.PurchaseServiceMapper;
import com.java.hotel.service.mapper.service.ServiceMapper;
import com.java.hotel.service.model.guest.Guest;
import com.java.hotel.service.model.purchase.Purchase;
import com.java.hotel.service.model.service.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ServiceService implements IServiceService {
    private final ServiceDAO serviceDao;
    private final GuestDAO guestDAO;
    private final PurchaseDAO purchaseDAO;
    private final ServiceMapper serviceMapper;
    private final PurchaseServiceMapper purchaseMapper;

    @Override
    @Transactional
    public ServiceDTO addService(ServiceDTO serviceDTO) {
        Service service = serviceMapper.toEntity(serviceDTO);
        serviceDao.save(service);
        return serviceMapper.toDto(service);
    }

    @Override
    public ServiceDTO findServiceById(Long serviceId) {

        Service service = serviceDao.findById(serviceId).orElseThrow((
                () -> new DataNotFoundException("Услуга с идентификатором " + serviceId + " не найдена!")
        ));

        return serviceMapper.toDto(service);
    }

    @Override
    public Page<ServiceDTO> getServices(CriteriaModel criteriaModel, Pageable pageable) {
        Specification<Service> specification = new GenericSpecification<>(criteriaModel, Service.class);
        Page<Service> rooms =  serviceDao.findAll(specification, pageable);
        return rooms.map(serviceMapper::toDto);
    }

    @Override
    @Transactional
    public void deleteService(Long serviceId) {
        serviceDao.findById(serviceId).orElseThrow((
                () -> new DataNotFoundException("Услуга с идентификатором " + serviceId + " не найдена!")
        ));
        serviceDao.deleteById(serviceId);
    }

    @Override
    @Transactional
    public ServiceDTO updatePriceService(RequestUpdateServicePriceDTO dto) {
        int newPrice = dto.getPrice();
        Long serviceId = dto.getServiceId();
        Service service = serviceDao.findById(serviceId).orElseThrow((
                () -> new DataNotFoundException("Услуга с идентификатором " + serviceId + " не найдена!")
        ));
        service.setPrice(newPrice);
        serviceDao.save(service);
        return serviceMapper.toDto(service);
    }

    @Override
    public long getCountAvailableService() {
        return serviceDao.countServicesByAvailableTrue();
    }

    @Override
    public Page<PurchaseServiceSimpleDTO> getPurchasedServicesByGuestId(Long guestId, Pageable page) {
        guestDAO.findById(guestId).orElseThrow(() ->
                new DataNotFoundException("Гость с идентификатором " + guestId + " не найден"));

        Page<Purchase> services = purchaseDAO.findPurchaseByGuest_IdOrderBy(guestId, page);
        return services.map(purchaseMapper::toSimpleDto);
    }

    @Override
    @Transactional
    public PurchaseServiceDTO purchaseService(RequestPurchaseServiceDTO dto) {

        LocalDate date = LocalDate.now();

        Long clientId = dto.getGuestId();

        Long serviceId = dto.getServiceId();

        Guest guest = guestDAO.findById(clientId).orElseThrow(()
                -> new DataNotFoundException("Клиент с идентификатором " + clientId + " не найден"));

        Service service = serviceDao.findById(serviceId).orElseThrow((
                () -> new DataNotFoundException("Услуга с идентификатором " + serviceId + " не найдена!")
        ));
        if (service.isAvailable()) {
            Purchase purchase = new Purchase(guest, service, date, date.plusDays(1));
            purchaseDAO.save(purchase);
            return purchaseMapper.toDto(purchase);

        } else {
            throw new DataAccessException("Услуга недоступна для покупки");
        }
    }
}
