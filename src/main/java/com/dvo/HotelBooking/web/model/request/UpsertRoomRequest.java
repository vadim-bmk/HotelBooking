package com.dvo.HotelBooking.web.model.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpsertRoomRequest {
    @NotBlank(message = "Название комнаты должно быть заполнено")
    private String name;
    @NotBlank(message = "Описание комнаты должно быть заполнено")
    private String description;
    @NotBlank(message = "Номер комнаты должен быть заполнен")
    private String number;

    @NotNull(message = "Стоимость комнаты должна быть заполнена")
    @DecimalMin(value = "0.0", inclusive = false, message = "Стоимость должна быть больше 0")
    private BigDecimal cost;

    @NotNull(message = "Максимально количество человек должно быть заполнено")
    @Min(value = 1, message = "Минимум один человек")
    private Integer maxPeople;

    private List<LocalDate> unavailableDates;

    @NotNull(message = "ID отеля должно быть заполнено")
    private Long hotelId;
}
