package com.java.hotel.service.mapper.service;

import com.java.hotel.service.dto.service.ServiceDTO;
import com.java.hotel.service.mapper.Mappable;
import com.java.hotel.service.model.service.Service;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ServiceMapper extends Mappable<Service, ServiceDTO> {
}
