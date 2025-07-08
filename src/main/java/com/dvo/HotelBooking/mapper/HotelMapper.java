package com.dvo.HotelBooking.mapper;

import com.dvo.HotelBooking.entity.Hotel;
import com.dvo.HotelBooking.web.model.request.UpdateHotelRequest;
import com.dvo.HotelBooking.web.model.request.UpsertHotelRequest;
import com.dvo.HotelBooking.web.model.response.HotelResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HotelMapper {
    HotelResponse hotelToResponse(Hotel hotel);

    Hotel requestToHotel(UpsertHotelRequest request);

    @Mapping(source = "hotelId", target = "id")
    Hotel requestToHotel(Long hotelId, UpsertHotelRequest request);

    void updateRequestToHotel(UpdateHotelRequest request, @MappingTarget Hotel hotel);
}
