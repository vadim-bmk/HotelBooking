package com.dvo.HotelBooking.web.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateHotelRequest {
    private String name;
    private String title;
    private String city;
    private String address;
    private Double distance;
}
