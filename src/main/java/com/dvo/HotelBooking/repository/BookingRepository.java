package com.dvo.HotelBooking.repository;

import com.dvo.HotelBooking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM bookings b " +
            "JOIN FETCH b.room r " +
            "JOIN FETCH r.hotel " +
            "JOIN FETCH b.user " +
            "WHERE b.id = :id")
    Optional<Booking> findByIdWithRoomAndHotel(@Param("id") Long id);
}
