package com.dvo.HotelBooking.repository;

import com.dvo.HotelBooking.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {
    void deleteAllByHotelId(Long hotelId);
}
