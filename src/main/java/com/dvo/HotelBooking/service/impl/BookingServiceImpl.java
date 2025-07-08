package com.dvo.HotelBooking.service.impl;

import com.dvo.HotelBooking.entity.Booking;
import com.dvo.HotelBooking.entity.Room;
import com.dvo.HotelBooking.entity.User;
import com.dvo.HotelBooking.entity.kafka.BookingEvent;
import com.dvo.HotelBooking.exception.EntityNotFoundException;
import com.dvo.HotelBooking.exception.RoomIsUnavailable;
import com.dvo.HotelBooking.mapper.BookingMapper;
import com.dvo.HotelBooking.repository.BookingRepository;
import com.dvo.HotelBooking.service.BookingService;
import com.dvo.HotelBooking.service.RoomService;
import com.dvo.HotelBooking.service.UserService;
import com.dvo.HotelBooking.web.model.request.UpdateBookingRequest;
import com.dvo.HotelBooking.web.model.request.UpsertBookingRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final RoomService roomService;
    private final UserService userService;
    private final BookingMapper bookingMapper;
    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;

    @Value("${app.kafka.kafkaBookingTopic}")
    private String topicName;

    @Override
    public List<Booking> findAll() {
        log.info("Call findAll in BookingServiceImpl");

        return bookingRepository.findAll();
    }

    @Override
    @Transactional
    public Booking findById(Long id) {
        log.info("Call findById in BookingServiceImpl with ID: {}", id);

        return bookingRepository.findByIdWithRoomAndHotel(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Бронирование с ID {0} не найдено!", id)));
    }

    @Override
    @Transactional
    public Booking createBooking(UpsertBookingRequest booking) {
        log.info("Call createBooking in BookingServiceImpl with booking: {}", booking);

        Room room = roomService.findById(booking.getRoomId());
        User user = userService.findByUsername(booking.getUsername());

        if (!isRoomAvailable(room.getId(), booking.getCheckInDate(), booking.getCheckOutDate())) {
            throw new RoomIsUnavailable(MessageFormat.format("Комната занята на указанные даты с {0} по {1}", booking.getCheckInDate(), booking.getCheckOutDate()));
        }

        List<LocalDate> reservedDates = getDatesBetween(booking.getCheckInDate(), booking.getCheckOutDate());

        room.getUnavailableDates().addAll(reservedDates);

        Booking newBooking = Booking.builder()
                .room(room)
                .user(user)
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .build();

        Booking savedBooking = bookingRepository.save(newBooking);

        BookingEvent event = new BookingEvent();
        event.setUserId(user.getId());
        event.setCheckInDate(savedBooking.getCheckInDate());
        event.setCheckOutDate(savedBooking.getCheckOutDate());
        kafkaTemplate.send(topicName, event);

        return savedBooking;
    }

    @Transactional
    @Override
    public Booking updateBooking(Long id, UpdateBookingRequest booking) {
        log.info("Call updateBooking in BookingServiceImpl with ID: {} and booking: {}", id, booking);

        Room existedRoom = roomService.findById(booking.getRoomId());

        Booking existedBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Бронирование с ID {0} не найдено!", id)));

        List<LocalDate> oldDates = getDatesBetween(existedBooking.getCheckInDate(), existedBooking.getCheckOutDate());
        existedRoom.getUnavailableDates().removeAll(oldDates);

        if (!isRoomAvailable(booking.getRoomId(), booking.getCheckInDate(), booking.getCheckOutDate())) {
            throw new RoomIsUnavailable(MessageFormat.format("Комната занята на указанные даты с {0} по {1}", booking.getCheckInDate(), booking.getCheckOutDate()));
        }

        List<LocalDate> newDates = getDatesBetween(booking.getCheckInDate(), booking.getCheckOutDate());
        existedRoom.getUnavailableDates().addAll(newDates);

        bookingMapper.updateRequestToBooking(booking, existedBooking, existedRoom);

        return bookingRepository.save(existedBooking);
    }

    @Override
    @Transactional
    public void deleteBooking(Long id) {
        log.info("Call deleteBooking in BookingServiceImpl with ID: {}", id);

        Booking existedBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Бронирование с ID {0} не найдено!", id)));

        List<LocalDate> oldDates = getDatesBetween(existedBooking.getCheckInDate(), existedBooking.getCheckOutDate());
        Room room = existedBooking.getRoom();
        room.getUnavailableDates().removeAll(oldDates);

        bookingRepository.deleteById(id);
    }

    @Override
    public boolean isRoomAvailable(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        return roomService.findById(roomId)
                .getUnavailableDates()
                .stream()
                .noneMatch(date -> !date.isBefore(checkIn) && !date.isAfter(checkOut.minusDays(1)));
    }

    private List<LocalDate> getDatesBetween(LocalDate start, LocalDate end) {
        return start.datesUntil(end).toList();
    }
}
