package com.dvo.HotelBooking.web.model.filter;

import com.dvo.HotelBooking.validation.RoomFilterValid;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@RoomFilterValid
public class RoomFilter {
    private Integer pageNumber;
    private Integer pageSize;

    private Long id;
    private String name;
    private String description;
    private String number;
    private BigDecimal minCost;
    private BigDecimal maxCost;
    private Integer maxPeople;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private Long hotelId;
}
