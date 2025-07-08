package com.dvo.HotelBooking.web.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpsertBookingRequest {
    @NotNull(message = "Номер комнаты должен быть указан")
    private Long roomId;

    @NotNull(message = "Имя пользователя должен быть указан")
    private String username;

    @NotNull(message = "Дата заселения должна быть указана")
    private LocalDate checkInDate;

    @NotNull(message = "Дата выселения должна быть указана")
    private LocalDate checkOutDate;

}
