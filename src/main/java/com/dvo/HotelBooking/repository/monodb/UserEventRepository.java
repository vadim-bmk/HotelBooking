package com.dvo.HotelBooking.repository.monodb;

import com.dvo.HotelBooking.entity.kafka.UserEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserEventRepository extends MongoRepository<UserEvent, String> {
}
