package com.dvo.HotelBooking.web.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpsertHotelRequest {
    @NotBlank(message = "Поле название (name) должно быть заполнено")
    private String name;

    @NotBlank(message = "Поле заголовок объявления (title) должно быть заполнено")
    private String title;

    @NotBlank(message = "Поле город (city) должно быть заполнено")
    private String city;

    @NotBlank(message = "Поле адрес (address) должно быть заполнено")
    private String address;

    @NotNull(message = "Поле расстояние от центра города (distance) должно быть заполнено")
    @Min(value = 0, message = "Поле расстояние от центра города (distance) должно быть больше 0")
    private Double distance;
}
