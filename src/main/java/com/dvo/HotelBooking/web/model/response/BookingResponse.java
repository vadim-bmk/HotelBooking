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
public class BookingResponse {
    private Long id;
    private RoomResponse room;
    private UserResponse user;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
