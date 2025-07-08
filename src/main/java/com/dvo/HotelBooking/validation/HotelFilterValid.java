package com.dvo.HotelBooking.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = HotelFilterValidValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HotelFilterValid {
    String message() default "Поля для пагинации должны быть указаны!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
