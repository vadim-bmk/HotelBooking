package com.dvo.HotelBooking.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RoomFilterValidValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RoomFilterValid {
    String message() default "Поля для пагинации должны быть указаны! " +
            "Если вы указываете minCost или maxCost, то оба поля должны быть указаны. " +
            "Если вы указываете checkInDate или checkOutDate, то оба поля должны быть указаны. checkOutDate должно быть больше checkInDate!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
