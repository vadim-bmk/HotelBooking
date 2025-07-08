package com.dvo.HotelBooking.web.controller;

import com.dvo.HotelBooking.entity.Booking;
import com.dvo.HotelBooking.mapper.BookingMapper;
import com.dvo.HotelBooking.service.BookingService;
import com.dvo.HotelBooking.web.model.request.UpsertBookingRequest;
import com.dvo.HotelBooking.web.model.response.BookingResponse;
import com.dvo.HotelBooking.web.model.response.BookingShortResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookController {
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get bookings",
            description = "Get all bookings",
            tags = {"booking"}
    )
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<BookingShortResponse>> findAll() {
        return ResponseEntity.ok(bookingService.findAll()
                .stream()
                .map(bookingMapper::bookingToShortResponse)
                .toList());
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get booking",
            description = "Get booking by ID",
            tags = {"booking"}
    )
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<BookingResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingMapper.bookingToResponse(bookingService.findById(id)));
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create booking",
            description = "Create booking",
            tags = {"booking"}
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<BookingResponse> createBooking(@RequestBody @Valid UpsertBookingRequest request) {
        Booking newBooking = bookingService.createBooking(request);

        return ResponseEntity.ok(bookingMapper.bookingToResponse(newBooking));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete booking",
            description = "Delete booking by ID",
            tags = {"booking"}
    )
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteById(@PathVariable Long id) {
        bookingService.deleteBooking(id);
    }

}
