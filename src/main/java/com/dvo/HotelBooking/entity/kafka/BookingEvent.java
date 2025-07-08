package com.dvo.HotelBooking.entity.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "bookingEvents")
public class BookingEvent {
    @Id
    private String id;
    private Long userId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
