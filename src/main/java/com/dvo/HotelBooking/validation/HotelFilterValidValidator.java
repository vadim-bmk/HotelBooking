package com.dvo.HotelBooking.validation;

import com.dvo.HotelBooking.web.model.filter.HotelFilter;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;

public class HotelFilterValidValidator implements ConstraintValidator<HotelFilterValid, HotelFilter> {
    @Override
    public boolean isValid(HotelFilter hotelFilter, ConstraintValidatorContext constraintValidatorContext) {
        if (ObjectUtils.anyNull(hotelFilter.getPageNumber(), hotelFilter.getPageSize())){
            return false;
        }

        return true;
    }
}
