package com.dvo.HotelBooking.service.impl;

import com.dvo.HotelBooking.entity.kafka.BookingEvent;
import com.dvo.HotelBooking.entity.kafka.UserEvent;
import com.dvo.HotelBooking.repository.monodb.BookingEventRepository;
import com.dvo.HotelBooking.repository.monodb.UserEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class StatisticsServiceImplTest {
    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    @Mock
    private UserEventRepository userEventRepository;

    @Mock
    private BookingEventRepository bookingEventRepository;

    @Test
    void testExportStatisticsToCsv() throws IOException {
        UserEvent userEvent = UserEvent.builder().userId(1L).build();
        BookingEvent bookingEvent = BookingEvent.builder()
                .userId(1L)
                .checkInDate(LocalDate.of(2025, 7, 1))
                .checkOutDate(LocalDate.of(2025, 7, 10))
                .build();

        when(userEventRepository.findAll()).thenReturn(List.of(userEvent));
        when(bookingEventRepository.findAll()).thenReturn(List.of(bookingEvent));

        String filePath = statisticsService.exportStatisticsToCsv();

        File file = new File(filePath);
        assertTrue(file.exists());

        List<String> lines = Files.readAllLines(file.toPath());

        assertEquals("\"Type\",\"User ID\",\"Check in Date\",\"Check out Date\"", lines.get(0));
        assertTrue(lines.stream().anyMatch(line -> line.contains("Booking")));
        assertTrue(lines.stream().anyMatch(line -> line.contains("User registration")));

        file.delete();
    }
}
