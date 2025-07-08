package com.dvo.HotelBooking.service;

import com.dvo.HotelBooking.entity.Room;
import com.dvo.HotelBooking.web.model.filter.RoomFilter;
import com.dvo.HotelBooking.web.model.request.UpsertRoomRequest;

import java.util.List;

public interface RoomService {
    List<Room> findAll();

    List<Room> findAllByFilter(RoomFilter filter);

    Room findById(Long id);

    Room create(Room room);

    Room update(Long id, UpsertRoomRequest room);

    void deleteById(Long id);
}
