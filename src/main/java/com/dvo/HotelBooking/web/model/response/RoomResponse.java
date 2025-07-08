package com.dvo.HotelBooking.web.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomResponse {
    private Long id;
    private String name;
    private String description;
    private String number;
    private BigDecimal cost;
    private Integer maxPeople;
    private List<LocalDate> unavailableDates;
    private Long hotelId;
}
