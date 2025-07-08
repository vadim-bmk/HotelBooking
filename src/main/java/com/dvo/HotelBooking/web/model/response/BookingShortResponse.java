package com.dvo.HotelBooking.web.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingShortResponse {
    private Long id;
    private String roomNumber;
    private String username;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
