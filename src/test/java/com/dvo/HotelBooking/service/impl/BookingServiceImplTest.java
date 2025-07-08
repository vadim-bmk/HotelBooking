package com.dvo.HotelBooking.service.impl;

import com.dvo.HotelBooking.entity.Booking;
import com.dvo.HotelBooking.entity.Room;
import com.dvo.HotelBooking.entity.User;
import com.dvo.HotelBooking.entity.kafka.BookingEvent;
import com.dvo.HotelBooking.exception.EntityNotFoundException;
import com.dvo.HotelBooking.exception.RoomIsUnavailable;
import com.dvo.HotelBooking.mapper.BookingMapper;
import com.dvo.HotelBooking.repository.BookingRepository;
import com.dvo.HotelBooking.service.RoomService;
import com.dvo.HotelBooking.service.UserService;
import com.dvo.HotelBooking.web.model.request.UpdateBookingRequest;
import com.dvo.HotelBooking.web.model.request.UpsertBookingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomService roomService;

    @Mock
    private UserService userService;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private KafkaTemplate<String, BookingEvent> kafkaTemplate;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(bookingService, "topicName", "test-topic");
    }

    @Test
    void testFindAll() {
        when(bookingRepository.findAll()).thenReturn(List.of(new Booking(), new Booking()));
        List<Booking> result = bookingService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(bookingRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        Booking booking = Booking.builder()
                .id(1L)
                .user(new User())
                .room(new Room())
                .build();

        when(bookingRepository.findByIdWithRoomAndHotel(1L)).thenReturn(Optional.of(booking));
        Booking result = bookingService.findById(1L);

        assertEquals(booking, result);
        verify(bookingRepository, times(1)).findByIdWithRoomAndHotel(1L);
    }

    @Test
    void testFindById_whenNotFound() {
        when(bookingRepository.findByIdWithRoomAndHotel(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.findById(1L));
    }

    @Test
    void testCreateBooking() {
        Room room = Room.builder().id(1L).name("room").build();
        room.setUnavailableDates(new ArrayList<>());

        User user = User.builder().id(1L).username("user").build();
        UpsertBookingRequest bookingRequest = UpsertBookingRequest.builder()
                .roomId(1L)
                .username("user")
                .checkInDate(LocalDate.of(2025, 6, 10))
                .checkOutDate(LocalDate.of(2025, 6, 15))
                .build();

        when(roomService.findById(1L)).thenReturn(room);
        when(userService.findByUsername("user")).thenReturn(user);
        when(bookingRepository.save(any(Booking.class))).thenReturn(new Booking());

        Booking result = bookingService.createBooking(bookingRequest);

        assertNotNull(result);
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(roomService, times(2)).findById(1L);
        verify(userService, times(1)).findByUsername("user");
        verify(kafkaTemplate).send(anyString(), any(BookingEvent.class));
    }

    @Test
    void testUpdateBooking() {
        Room room = Room.builder()
                .id(2L)
                .name("room")
                .unavailableDates(new ArrayList<>(List.of(
                        LocalDate.of(2025, 6, 10),
                        LocalDate.of(2025, 6, 11))))
                .build();

        Booking existingBooking = Booking.builder()
                .id(1L)
                .user(new User())
                .room(room)
                .checkInDate(LocalDate.of(2025, 6, 1))
                .checkOutDate(LocalDate.of(2025, 6, 5))
                .build();
        UpdateBookingRequest request = UpdateBookingRequest.builder()
                .roomId(2L)
                .checkInDate(LocalDate.of(2025, 6, 13))
                .checkOutDate(LocalDate.of(2025, 6, 15))
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking));
        when(roomService.findById(2L)).thenReturn(room);
        when(bookingRepository.save(any(Booking.class))).thenReturn(existingBooking);

        Booking result = bookingService.updateBooking(1L, request);

        assertNotNull(result);
        verify(bookingRepository).save(existingBooking);
        verify(bookingMapper).updateRequestToBooking(request, existingBooking, room);
    }

    @Test
    void testUpdateBooking_whenBookingNotFound() {
        UpdateBookingRequest request = UpdateBookingRequest.builder()
                .roomId(1L)
                .checkInDate(LocalDate.of(2025, 7, 10))
                .checkOutDate(LocalDate.of(2025, 7, 12))
                .build();

        when(roomService.findById(anyLong())).thenReturn(new Room());
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> bookingService.updateBooking(anyLong(), request));
    }

    @Test
    void testUpdateBooking_whenNotAvailableRoom() {
        Room room = Room.builder()
                .id(2L)
                .name("room")
                .unavailableDates(new ArrayList<>(List.of(
                        LocalDate.of(2025, 6, 10),
                        LocalDate.of(2025, 6, 11))))
                .build();

        Booking existingBooking = Booking.builder()
                .id(1L)
                .user(new User())
                .room(room)
                .checkInDate(LocalDate.of(2025, 6, 1))
                .checkOutDate(LocalDate.of(2025, 6, 5))
                .build();
        UpdateBookingRequest request = UpdateBookingRequest.builder()
                .roomId(2L)
                .checkInDate(LocalDate.of(2025, 6, 10))
                .checkOutDate(LocalDate.of(2025, 6, 11))
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking));
        when(roomService.findById(2L)).thenReturn(room);

        assertThrows(RoomIsUnavailable.class, () -> bookingService.updateBooking(1L, request));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testDeleteBooking() {
        Room room = Room.builder()
                .id(2L)
                .name("room")
                .unavailableDates(new ArrayList<>(List.of(
                        LocalDate.of(2025, 6, 10),
                        LocalDate.of(2025, 6, 11))))
                .build();

        Booking existingBooking = Booking.builder()
                .id(1L)
                .user(new User())
                .room(room)
                .checkInDate(LocalDate.of(2025, 6, 1))
                .checkOutDate(LocalDate.of(2025, 6, 5))
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking));
        bookingService.deleteBooking(1L);
        verify(bookingRepository).deleteById(1L);
    }

    @Test
    void testDeleteBooking_whenBookingNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> bookingService.deleteBooking(1L));
    }
}
