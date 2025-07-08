package com.dvo.HotelBooking.web.controller;

import com.dvo.HotelBooking.entity.Hotel;
import com.dvo.HotelBooking.entity.Room;
import com.dvo.HotelBooking.mapper.RoomMapper;
import com.dvo.HotelBooking.service.HotelService;
import com.dvo.HotelBooking.service.RoomService;
import com.dvo.HotelBooking.web.model.filter.RoomFilter;
import com.dvo.HotelBooking.web.model.request.UpsertRoomRequest;
import com.dvo.HotelBooking.web.model.response.ModelListResponse;
import com.dvo.HotelBooking.web.model.response.RoomResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/room")
public class RoomController {
    private final RoomService roomService;
    private final RoomMapper roomMapper;
    private final HotelService hotelService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get room",
            description = "Get all rooms by filter",
            tags = {"room"}
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<ModelListResponse<RoomResponse>> findAllByFilter(@Valid RoomFilter filter) {
        List<Room> roomList = roomService.findAllByFilter(filter);
        ModelListResponse<RoomResponse> response = ModelListResponse.<RoomResponse>builder()
                .totalCount((long) roomList.size())
                .data(roomList.stream().map(roomMapper::roomToResponse).toList())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get room",
            description = "Get room by ID.",
            tags = {"room"}
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<RoomResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(roomMapper.roomToResponse(roomService.findById(id)));
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create room",
            description = "Create room",
            tags = {"room"}
    )
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<RoomResponse> create(@RequestBody @Valid UpsertRoomRequest request) {
        Hotel hotel = hotelService.findById(request.getHotelId());
        Room newRoom = roomMapper.requestToRoom(request, hotel);

        return ResponseEntity.ok(roomMapper.roomToResponse(roomService.create(newRoom)));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Update room",
            description = "Update room by ID",
            tags = {"room"}
    )
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<RoomResponse> update(@PathVariable Long id,
                                               @RequestBody @Valid UpsertRoomRequest request) {

        Room updatedRoom = roomService.update(id, request);

        return ResponseEntity.ok(roomMapper.roomToResponse(updatedRoom));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete room",
            description = "Delete room by ID",
            tags = {"room"}
    )
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteById(@PathVariable Long id) {
        roomService.deleteById(id);
    }
}
