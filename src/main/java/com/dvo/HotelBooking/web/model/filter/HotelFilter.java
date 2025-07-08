package com.dvo.HotelBooking.web.model.filter;

import com.dvo.HotelBooking.validation.HotelFilterValid;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@HotelFilterValid
public class HotelFilter {
    private Integer pageNumber;
    private Integer pageSize;

    private Long id;
    private String name;
    private String title;
    private String city;
    private String address;
    private Double distance;
    private Double rating;
    private Integer numberOfRating;
}
