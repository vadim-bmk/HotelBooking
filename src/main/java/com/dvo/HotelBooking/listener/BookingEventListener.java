package com.dvo.HotelBooking.listener;

import com.dvo.HotelBooking.entity.kafka.BookingEvent;
import com.dvo.HotelBooking.repository.monodb.BookingEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookingEventListener {
    private final BookingEventRepository bookingEventRepository;

    @Value("${app.kafka.kafkaBookingTopic}")
    private String topicName;

    @KafkaListener(topics = "${app.kafka.kafkaBookingTopic}",
            groupId = "${app.kafka.kafkaGroupId}",
            containerFactory = "bookingKafkaListenerContainerFactory")
    public void listen(@Payload BookingEvent message,
                       @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) UUID key,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                       @Header(KafkaHeaders.RECEIVED_TIMESTAMP) Long timestamp) {

        bookingEventRepository.save(message);

        log.info("Received message: {}", message);
    }
}
