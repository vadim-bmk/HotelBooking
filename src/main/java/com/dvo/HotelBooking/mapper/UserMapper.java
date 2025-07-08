package com.dvo.HotelBooking.mapper;

import com.dvo.HotelBooking.entity.User;
import com.dvo.HotelBooking.web.model.request.UpdateUserRequest;
import com.dvo.HotelBooking.web.model.request.UpsertUserRequest;
import com.dvo.HotelBooking.web.model.response.UserResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(target = "role", source = "roleType")
    UserResponse userToResponse(User user);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "roleType", ignore = true)
    })
    User requestToUser(UpsertUserRequest request);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "username", ignore = true),
            @Mapping(target = "roleType", ignore = true)
    })
    void updateRequestToUser(UpdateUserRequest request, @MappingTarget User user);
}
