package com.dvo.HotelBooking.service;

import com.dvo.HotelBooking.entity.Booking;
import com.dvo.HotelBooking.web.model.request.UpdateBookingRequest;
import com.dvo.HotelBooking.web.model.request.UpsertBookingRequest;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    List<Booking> findAll();

    Booking findById(Long id);

    Booking createBooking(UpsertBookingRequest booking);

    void deleteBooking(Long id);

    boolean isRoomAvailable(Long roomId, LocalDate checkIn, LocalDate checkOut);

    Booking updateBooking(Long id, UpdateBookingRequest booking);
}
