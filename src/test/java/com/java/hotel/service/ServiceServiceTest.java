package com.java.hotel.service;

import com.java.hotel.dao.GuestDAO;
import com.java.hotel.dao.PurchaseDAO;
import com.java.hotel.dao.ServiceDAO;
import com.java.hotel.dao.exception.DataAccessException;
import com.java.hotel.dao.exception.DataNotFoundException;
import com.java.hotel.service.dto.service.PurchaseServiceDTO;
import com.java.hotel.service.dto.service.RequestPurchaseServiceDTO;
import com.java.hotel.service.dto.service.RequestUpdateServicePriceDTO;
import com.java.hotel.service.dto.service.ServiceDTO;
import com.java.hotel.service.impl.ServiceService;
import com.java.hotel.service.mapper.service.PurchaseServiceMapper;
import com.java.hotel.service.mapper.service.ServiceMapper;
import com.java.hotel.service.model.guest.Guest;
import com.java.hotel.service.model.purchase.Purchase;
import com.java.hotel.service.model.service.Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static com.java.hotel.service.model.service.Category.TECHNIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceServiceTest {

    @Mock
    private ServiceDAO serviceDAO;

    @Mock
    private GuestDAO guestDAO;

    @Mock
    private PurchaseDAO purchaseDAO;

    @Mock
    private ServiceMapper serviceMapper;

    @Mock
    private PurchaseServiceMapper purchaseServiceMapper;

    @InjectMocks
    private ServiceService serviceService;


    @Test
    void testAddService() {
        Service service = mock(Service.class);
        ServiceDTO serviceDTO = mock(ServiceDTO.class);
        when(serviceMapper.toEntity(any(ServiceDTO.class))).thenReturn(service);
        when(serviceDAO.save(any(Service.class))).thenReturn(service);
        when(serviceMapper.toDto(any(Service.class))).thenReturn(serviceDTO);

        ServiceDTO result = serviceService.addService(serviceDTO);

        assertEquals(serviceDTO, result);
        verify(serviceMapper, times(1)).toEntity(any(ServiceDTO.class));
        verify(serviceDAO, times(1)).save(any(Service.class));
        verify(serviceMapper, times(1)).toDto(any(Service.class));
    }


    @Test
    void testGetServiceById() {
        Long serviceId = 1L;
        ServiceDTO serviceDTO = mock(ServiceDTO.class);
        Service service = mock(Service.class);

        when(serviceDAO.findById(serviceId)).thenReturn(Optional.of(service));
        when(serviceMapper.toDto(any(Service.class))).thenReturn(serviceDTO);
        ServiceDTO result = serviceService.findServiceById(serviceId);

        assertThat(result).isNotNull();
        assertEquals(serviceDTO, result);

        verify(serviceDAO, times(1)).findById(serviceId);
        verify(serviceMapper, times(1)).toDto(any(Service.class));
    }


    @Test
    void testGetServiceById_ThrowsDataNotFoundException() {
        assertThrows(DataNotFoundException.class, () -> serviceService.findServiceById(100000L));
    }


    @Test
    void testDeleteService() {
        Service service = mock(Service.class);

        when(serviceDAO.findById(anyLong())).thenReturn(Optional.of(service));

        serviceService.deleteService(anyLong());

        verify(serviceDAO, times(1)).findById(anyLong());
        verify(serviceDAO, times(1)).deleteById(anyLong());
    }

    @Test
    void testUpdatePriceService() {
        Service service = mock(Service.class);
        ServiceDTO serviceDTO = mock(ServiceDTO.class);
        RequestUpdateServicePriceDTO dto = new RequestUpdateServicePriceDTO(1L, 200);

        when(serviceDAO.findById(anyLong())).thenReturn(Optional.of(service));
        when(serviceDAO.save(any(Service.class))).thenReturn(service);
        when(serviceMapper.toDto(any(Service.class))).thenReturn(serviceDTO);

        ServiceDTO result = serviceService.updatePriceService(dto);
        assertThat(result).isNotNull();
        assertEquals(serviceDTO, result);

        verify(serviceDAO, times(1)).findById(anyLong());
        verify(serviceDAO, times(1)).save(any(Service.class));
        verify(serviceMapper, times(1)).toDto(any(Service.class));
    }

    @Test
    void testGetCountAvailableService() {
        long expectedCount = 5L;
        when(serviceDAO.countServicesByAvailableTrue()).thenReturn(expectedCount);
        long result = serviceService.getCountAvailableService();

        assertEquals(expectedCount, result);

        verify(serviceDAO, times(1)).countServicesByAvailableTrue();
    }

    @Test
    void testGetPurchasedServicesByGuestId() {
        Long guestId = 1L;
        Pageable page = PageRequest.of(0, 10);
        Guest guest = mock(Guest.class);

        when(guestDAO.findById(anyLong())).thenReturn(Optional.of(guest));
        Page<Purchase> purchases = new PageImpl<>(Collections.emptyList());

        when(purchaseDAO.findPurchaseByGuest_IdOrderBy(guestId, page)).thenReturn(purchases);
        serviceService.getPurchasedServicesByGuestId(guestId, page);

        verify(guestDAO, times(1)).findById(guestId);
        verify(purchaseDAO, times(1)).findPurchaseByGuest_IdOrderBy(guestId, page);
    }

    @Test
    void testPurchaseService() {
        RequestPurchaseServiceDTO dto = new RequestPurchaseServiceDTO(1L, 1L);
        LocalDate date = LocalDate.now();
        Guest guest = mock(Guest.class);

        Service service = Service.builder()
                .id(1L)
                .title("Утюг")
                .price(100)
                .category(TECHNIC)
                .build();

        service.setAvailable(true);

        when(guestDAO.findById(anyLong())).thenReturn(Optional.of(guest));
        when(serviceDAO.findById(anyLong())).thenReturn(Optional.of(service));

        Purchase purchase = new Purchase(guest, service, date, date.plusDays(1));
        when(purchaseDAO.save(any(Purchase.class))).thenReturn(purchase);

        PurchaseServiceDTO purchaseServiceDTO = mock(PurchaseServiceDTO.class);
        when(purchaseServiceMapper.toDto(any(Purchase.class))).thenReturn(purchaseServiceDTO);

        PurchaseServiceDTO result = serviceService.purchaseService(dto);

        assertNotNull(result);
        assertEquals(purchaseServiceDTO, result);

        verify(guestDAO, times(1)).findById(anyLong());
        verify(serviceDAO, times(1)).findById(anyLong());
        verify(purchaseDAO, times(1)).save(any(Purchase.class));
        verify(purchaseServiceMapper, times(1)).toDto(any(Purchase.class));
    }

    @Test
    void testGetPurchasedServicesByGuestId_ThrowsDataNotFoundException() {
        Long guestId = 1L;
        Pageable page = PageRequest.of(0, 10);
        when(guestDAO.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> serviceService.getPurchasedServicesByGuestId(guestId, page));

        verify(guestDAO, times(1)).findById(anyLong());
        verify(purchaseDAO, never()).findPurchaseByGuest_IdOrderBy(anyLong(), any(Pageable.class));
    }

    @Test
    void testPurchaseService_ThrowsDataNotFoundExceptionForGuest() {
        RequestPurchaseServiceDTO dto = new RequestPurchaseServiceDTO(1L, 1L);
        when(guestDAO.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> serviceService.purchaseService(dto));

        verify(guestDAO, times(1)).findById(anyLong());
        verify(serviceDAO, never()).findById(anyLong());
        verify(purchaseDAO, never()).save(any(Purchase.class));
        verify(purchaseServiceMapper, never()).toDto(any(Purchase.class));
    }

    @Test
    void testPurchaseService_ThrowsDataNotFoundExceptionForService() {
        RequestPurchaseServiceDTO dto = new RequestPurchaseServiceDTO(1L, 1L);
        Guest guest = mock(Guest.class);

        when(guestDAO.findById(anyLong())).thenReturn(Optional.of(guest));
        when(serviceDAO.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> serviceService.purchaseService(dto));

        verify(guestDAO, times(1)).findById(anyLong());
        verify(serviceDAO, times(1)).findById(anyLong());
        verify(purchaseDAO, never()).save(any(Purchase.class));
        verify(purchaseServiceMapper, never()).toDto(any(Purchase.class));
    }

    @Test
    void testPurchaseServiceThrowsDataAccessException() {
        RequestPurchaseServiceDTO dto = new RequestPurchaseServiceDTO(1L, 1L);
        Guest guest = mock(Guest.class);
        Service service = mock(Service.class);
        service.setAvailable(false);

        when(guestDAO.findById(anyLong())).thenReturn(Optional.of(guest));
        when(serviceDAO.findById(anyLong())).thenReturn(Optional.of(service));

        assertThrows(DataAccessException.class, () -> serviceService.purchaseService(dto));

        verify(guestDAO, times(1)).findById(anyLong());
        verify(serviceDAO, times(1)).findById(anyLong());
        verify(purchaseDAO, never()).save(any(Purchase.class));
        verify(purchaseServiceMapper, never()).toDto(any(Purchase.class));
    }
}
