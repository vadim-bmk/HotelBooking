package com.dvo.HotelBooking.web.controller;


import com.dvo.HotelBooking.entity.Hotel;
import com.dvo.HotelBooking.mapper.HotelMapper;
import com.dvo.HotelBooking.service.HotelService;
import com.dvo.HotelBooking.web.model.filter.HotelFilter;
import com.dvo.HotelBooking.web.model.request.UpdateHotelRequest;
import com.dvo.HotelBooking.web.model.request.UpsertHotelRequest;
import com.dvo.HotelBooking.web.model.response.HotelResponse;
import com.dvo.HotelBooking.web.model.response.ModelListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hotel")
public class HotelController {
    private final HotelService hotelService;
    private final HotelMapper hotelMapper;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get hotels",
            description = "Get all hotels by pagination and filter request",
            tags = {"hotel"}
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<ModelListResponse<HotelResponse>> findAllByFilter(@Valid HotelFilter filter) {
        List<Hotel> hotelList = hotelService.findAllByFilter(filter);
        ModelListResponse<HotelResponse> response = ModelListResponse.<HotelResponse>builder()
                .totalCount((long) hotelList.size())
                .data(hotelList.stream().map(hotelMapper::hotelToResponse).toList())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get hotel",
            description = "Get hotel by ID. Return ID, name, title, city, address, distance, rating and numberOfRating",
            tags = {"hotel"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = {@Content(schema = @Schema(implementation = HotelResponse.class), mediaType = "application/json")}
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = {@Content(schema = @Schema(implementation = HotelResponse.class), mediaType = "application/json")}
            )
    })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<HotelResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(hotelMapper.hotelToResponse(hotelService.findById(id)));

    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create hotel",
            description = "Create hotel",
            tags = {"hotel"}
    )
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<HotelResponse> createHotel(@RequestBody @Valid UpsertHotelRequest request) {
        Hotel newHotel = hotelService.create(hotelMapper.requestToHotel(request));

        return ResponseEntity.ok(hotelMapper.hotelToResponse(newHotel));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Update hotel",
            description = "Update hotel by ID",
            tags = {"hotel"}
    )
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<HotelResponse> updateHotel(@PathVariable Long id,
                                                     @RequestBody @Valid UpdateHotelRequest request) {

        Hotel existedHotel = hotelService.findById(id);
        hotelMapper.updateRequestToHotel(request, existedHotel);
        Hotel updatedHotel = hotelService.update(id, existedHotel);
        return ResponseEntity.ok(hotelMapper.hotelToResponse(updatedHotel));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete hotel",
            description = "Delete hotel by ID",
            tags = {"hotel"}
    )
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteById(@PathVariable Long id) {
        hotelService.deleteById(id);
    }

    @PutMapping("/{id}/rating")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Set rating",
            description = "Set new mark for hotel by ID",
            tags = {"hotel"}
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public void setRating(@PathVariable Long id,
                          @RequestParam Integer mark){
        if (mark < 1 || mark > 5){
            throw new IllegalArgumentException("Допустимые оценки от 1 до 5");
        }
        hotelService.setRating(id, mark);
    }
}
