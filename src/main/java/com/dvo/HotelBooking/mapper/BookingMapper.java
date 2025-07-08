package com.dvo.HotelBooking.mapper;

import com.dvo.HotelBooking.entity.Booking;
import com.dvo.HotelBooking.entity.Room;
import com.dvo.HotelBooking.entity.User;
import com.dvo.HotelBooking.web.model.request.UpdateBookingRequest;
import com.dvo.HotelBooking.web.model.request.UpsertBookingRequest;
import com.dvo.HotelBooking.web.model.response.BookingResponse;
import com.dvo.HotelBooking.web.model.response.BookingShortResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookingMapper {
    @Mapping(source = "room.hotel.id", target = "room.hotelId")
    BookingResponse bookingToResponse(Booking booking);

    @Mapping(source = "room.number", target = "roomNumber")
    @Mapping(source = "user.username", target = "username")
    BookingShortResponse bookingToShortResponse(Booking booking);

    @Mapping(source = "room", target = "room")
    @Mapping(source = "user", target = "user")
    @Mapping(target = "id", ignore = true)
    Booking requestToBooking(UpsertBookingRequest request, Room room, User user);

    @Mapping(source = "room", target = "room")
    void updateRequestToBooking(UpdateBookingRequest request, @MappingTarget Booking booking, Room room);
}
