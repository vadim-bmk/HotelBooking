package com.dvo.HotelBooking.validation;

import com.dvo.HotelBooking.web.model.filter.RoomFilter;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;

public class RoomFilterValidValidator implements ConstraintValidator<RoomFilterValid, RoomFilter> {

    @Override
    public boolean isValid(RoomFilter roomFilter, ConstraintValidatorContext constraintValidatorContext) {
        if (ObjectUtils.anyNull(roomFilter.getPageNumber(), roomFilter.getPageSize())) {
            return false;
        }

        if (roomFilter.getMinCost() == null && roomFilter.getMaxCost() != null ||
                roomFilter.getMinCost() != null && roomFilter.getMaxCost() == null) {
            return false;
        }

        if (roomFilter.getCheckInDate() == null && roomFilter.getCheckOutDate() != null ||
                roomFilter.getCheckInDate() != null && roomFilter.getCheckOutDate() == null) {
            return false;
        }
        if (roomFilter.getCheckInDate() != null) {
            if (roomFilter.getCheckInDate().isAfter(roomFilter.getCheckOutDate())) {
                return false;
            }
        }

        return true;
    }
}
