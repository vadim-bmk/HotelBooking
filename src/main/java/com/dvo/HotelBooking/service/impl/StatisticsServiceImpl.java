package com.dvo.HotelBooking.service.impl;

import com.opencsv.CSVWriter;
import com.dvo.HotelBooking.entity.kafka.BookingEvent;
import com.dvo.HotelBooking.entity.kafka.UserEvent;
import com.dvo.HotelBooking.repository.monodb.BookingEventRepository;
import com.dvo.HotelBooking.repository.monodb.UserEventRepository;
import com.dvo.HotelBooking.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final UserEventRepository userEventRepository;
    private final BookingEventRepository bookingEventRepository;

    @Override
    public String exportStatisticsToCsv() {
        String csvFile = "export.csv";
        List<UserEvent> userEvents = userEventRepository.findAll();
        List<BookingEvent> bookingEvents = bookingEventRepository.findAll();

        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
            writer.writeNext(new String[]{"Type", "User ID", "Check in Date", "Check out Date"});

            for (BookingEvent event : bookingEvents) {
                writer.writeNext(new String[]{
                        "Booking",
                        event.getUserId().toString(),
                        event.getCheckInDate() != null ? event.getCheckInDate().toString() : "",
                        event.getCheckOutDate() != null ? event.getCheckOutDate().toString() : ""
                });
            }

            for (UserEvent event : userEvents) {
                writer.writeNext(new String[]{
                        "User registration",
                        event.getUserId().toString()
                });
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return csvFile;
    }
}
