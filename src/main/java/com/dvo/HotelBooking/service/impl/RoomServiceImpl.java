package com.dvo.HotelBooking.service.impl;

import com.dvo.HotelBooking.entity.Room;
import com.dvo.HotelBooking.exception.EntityNotFoundException;
import com.dvo.HotelBooking.mapper.RoomMapper;
import com.dvo.HotelBooking.repository.HotelRepository;
import com.dvo.HotelBooking.repository.RoomRepository;
import com.dvo.HotelBooking.repository.RoomSpecification;
import com.dvo.HotelBooking.service.RoomService;
import com.dvo.HotelBooking.web.model.filter.RoomFilter;
import com.dvo.HotelBooking.web.model.request.UpsertRoomRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final RoomMapper roomMapper;

    @Override
    public List<Room> findAll() {
        log.info("Call findAll in RoomServiceImpl");

        return roomRepository.findAll();
    }

    @Override
    public List<Room> findAllByFilter(RoomFilter filter) {
        log.info("Call findAllByFilter in RoomServiceImpl with filter: {}", filter);

        return roomRepository.findAll(
                RoomSpecification.withFilter(filter),
                PageRequest.of(filter.getPageNumber(), filter.getPageSize())
        ).getContent();
    }

    @Override
    public Room findById(Long id) {
        log.info("Call findById in RoomServiceImpl with ID: {}", id);

        return roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Комната с ID {0} не найдена!", id)));
    }

    @Override
    @Transactional
    public Room create(Room room) {
        log.info("Call create in RoomServiceImpl with room: {}", room);

        return roomRepository.save(room);
    }

    @Override
    @Transactional
    public Room update(Long id, UpsertRoomRequest room) {
        log.info("Call update in RoomServiceImpl ");

        Room existedRoom = roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Комната с ID {0} не найдена", id)));

        roomMapper.updateRoomFromRequest(room, existedRoom);

//        existedRoom.setName(room.getName());
//        existedRoom.setDescription(room.getDescription());
//        existedRoom.setNumber(room.getNumber());
//        existedRoom.setCost(room.getCost());
//        existedRoom.setMaxPeople(room.getMaxPeople());
        if (room.getUnavailableDates() != null) {
            existedRoom.getUnavailableDates().clear();
            existedRoom.getUnavailableDates().addAll(room.getUnavailableDates());
        }

        return roomRepository.save(existedRoom);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Call deleteById in RoomServiceImpl with ID: {}", id);

        roomRepository.deleteById(id);
    }
}
