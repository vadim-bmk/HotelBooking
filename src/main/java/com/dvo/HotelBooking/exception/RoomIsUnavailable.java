package com.dvo.HotelBooking.exception;

public class RoomIsUnavailable extends RuntimeException {
    public RoomIsUnavailable(String message) {
        super(message);
    }
}
