package com.java.hotel.service.impl;

import com.java.hotel.dao.PurchaseDAO;
import com.java.hotel.dao.RoomDAO;
import com.java.hotel.dao.exception.*;
import com.java.hotel.service.IRoomService;
import com.java.hotel.service.dto.room.RequestUpdateRoomPriceDTO;
import com.java.hotel.service.dto.room.RoomDTO;
import com.java.hotel.service.filter.CriteriaModel;
import com.java.hotel.service.filter.GenericSpecification;
import com.java.hotel.service.mapper.room.RoomMapper;
import com.java.hotel.service.model.residence.Residence;
import com.java.hotel.service.model.residence.Residence_;
import com.java.hotel.service.model.room.Room;
import com.java.hotel.service.model.room.Room_;
import com.java.hotel.service.model.room.State;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.java.hotel.service.model.room.State.*;
import static org.springframework.data.jpa.domain.Specification.where;

@Slf4j
@Service
public class RoomService implements IRoomService {
    private final RoomDAO roomDao;
    private final PurchaseDAO purchaseDAO;
    private final RoomMapper roomMapper;
    private final String changeRoomStatusEnabled;

    @Autowired
    public RoomService(RoomDAO roomDao,
                       PurchaseDAO purchaseDAO,
                       RoomMapper roomMapper,
                       @Value("${changeRoomStatusEnabled}") String changeRoomStatusEnabled) {

        this.roomDao = roomDao;
        this.purchaseDAO = purchaseDAO;
        this.roomMapper = roomMapper;
        this.changeRoomStatusEnabled = changeRoomStatusEnabled;
    }

    @Override
    @Transactional
    public RoomDTO addRoom(RoomDTO roomDTO) {
        Room room = roomMapper.toEntity(roomDTO);
        Room savedRoom = roomDao.save(room);
        return roomMapper.toDto(savedRoom);
    }

    @Override
    public Page<RoomDTO> getRooms(CriteriaModel criteriaModel, Pageable pageable) {
        Specification<Room> specification = new GenericSpecification<>(criteriaModel, Room.class);
        Page<Room> rooms =  roomDao.findAll(specification, pageable);
        return rooms.map(roomMapper::toDto);
    }

    @Override
    public Page<RoomDTO> getRoomsOnDate(LocalDate date, Pageable pageable) {
        Page<Room> rooms = roomDao.findAll(where(hasAvailableDate(date)), pageable);
        return rooms.map(roomMapper::toDto);
    }

    @Override
    @Transactional
    public void deleteRoom(Long roomId) {
        Room room = roomDao.findById(roomId).orElseThrow(() -> new DataNotFoundException("Комната с идентификатором " + roomId + " не найдена!"));
        if (!room.getState().equals(State.OCCUPIED)) {
            roomDao.deleteById(roomId);
        } else {
            log.error("Нельзя удалить заселенный номер!");
            throw new DataDeleteException("Нельзя удалить заселенный номер");
        }
    }

    @Override
    @Transactional
    public RoomDTO updatePriceRoom(RequestUpdateRoomPriceDTO dto) {
        Long roomId = dto.getRoomId();
        int newPrice = dto.getPrice();
        Room room = roomDao.findById(roomId).orElseThrow(()
                -> new DataNotFoundException("Комната с идентификатором " + roomId + " не найдена!"));
        if (room.getState().equals(OCCUPIED)) {
            log.error("Нельзя поменять цену уже заселенной комнаты!");
            throw new DataSaveException("Нельзя поменять цену уже заселенной комнаты!");
        }
        room.setPrice(newPrice);
        roomDao.save(room);
        return roomMapper.toDto(room);
    }

    @Override
    @Transactional
    public RoomDTO closeRoomForRepairs(Long roomId) {
        return updateRoomStatus(roomId, BEING_REPAIRED);
    }

    @Override
    @Transactional
    public RoomDTO openRoomAfterRepairs(Long roomId) {
        return updateRoomStatus(roomId, FREE);
    }

    @Override
    public long getCountFreeRooms() {
        return roomDao.countRoomsByState_Free();
    }

    @Override
    public RoomDTO getRoomById(Long roomId) {
        Room room = roomDao.findById(roomId).orElseThrow(()
                -> new DataNotFoundException("Комната с идентификатором " + roomId + " не найдена!"));
        return roomMapper.toDto(room);
    }

    @Override
    public int getTotalCostForRoom(Long roomId) {
        return purchaseDAO.findTotalCostForByRoomId(roomId);
    }

    private RoomDTO updateRoomStatus(Long roomId, State newStatus) {
        if (Boolean.parseBoolean(changeRoomStatusEnabled)) {
            Room room = roomDao.findById(roomId).orElseThrow(()
                    -> new DataNotFoundException("Комната с идентификатором " + roomId + " не найдена!"));
            if (room.getState().equals(OCCUPIED)) {
                log.error("Нельзя поменять статус заселенной комнаты!");
                throw new DataAccessException("Нельзя поменять статус заселенной комнаты!");
            }
            if (room.getState().equals(newStatus)) {
                log.error("Статус комнаты не изменился!");
                throw new DataUpdateException("Статус комнаты не изменился!");
            }
            room.setState(newStatus);
            roomDao.save(room);
            log.info("Статус комнаты обновлен!");
            return roomMapper.toDto(room);
        } else {
            throw new DataUpdateException("Обновление статуса комнаты запрещено администратором!");
        }
    }

    private Specification<Room> hasAvailableDate(LocalDate date) {
        return (root, query, criteriaBuilder) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Residence> subRoot = subquery.from(Residence.class);
            subquery.select(subRoot.get(Residence_.ROOM));
            Predicate checkinPredicate = criteriaBuilder.lessThanOrEqualTo(subRoot.get(Residence_.DATE_CHECK_IN), date);
            Predicate checkoutPredicate = criteriaBuilder.greaterThanOrEqualTo(subRoot.get(Residence_.DATE_CHECK_OUT), date);
            subquery.where(criteriaBuilder.and(checkinPredicate, checkoutPredicate));
            return criteriaBuilder.not(root.get(Room_.ID).in(subquery));
        };
    }
}