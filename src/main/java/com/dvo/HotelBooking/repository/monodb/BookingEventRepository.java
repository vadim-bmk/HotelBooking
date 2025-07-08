package com.dvo.HotelBooking.repository.monodb;

import com.dvo.HotelBooking.entity.kafka.BookingEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookingEventRepository extends MongoRepository<BookingEvent, Long> {
}
