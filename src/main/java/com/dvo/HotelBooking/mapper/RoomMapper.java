package com.dvo.HotelBooking.mapper;

import com.dvo.HotelBooking.entity.Hotel;
import com.dvo.HotelBooking.entity.Room;
import com.dvo.HotelBooking.web.model.request.UpsertRoomRequest;
import com.dvo.HotelBooking.web.model.response.RoomResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoomMapper {
    @Mapping(source = "hotel.id", target = "hotelId")
    RoomResponse roomToResponse(Room room);

    @Mapping(source = "hotel", target = "hotel")
    @Mapping(source = "request.name", target = "name")
    @Mapping(target = "id", ignore = true)
    Room requestToRoom(UpsertRoomRequest request, Hotel hotel);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRoomFromRequest(UpsertRoomRequest request, @MappingTarget Room room);
}
