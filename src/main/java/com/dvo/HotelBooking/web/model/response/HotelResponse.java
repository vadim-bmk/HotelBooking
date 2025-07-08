package com.dvo.HotelBooking.web.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelResponse {
    private Long id;
    private String name;
    private String title;
    private String city;
    private String address;
    private Double distance;
    private Double rating;
    private Integer numberOfRating;
}
