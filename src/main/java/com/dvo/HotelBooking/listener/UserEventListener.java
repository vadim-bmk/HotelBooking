package com.dvo.HotelBooking.listener;

import com.dvo.HotelBooking.entity.kafka.UserEvent;
import com.dvo.HotelBooking.repository.monodb.UserEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {
    private final UserEventRepository userEventRepository;

    @Value("${app.kafka.kafkaUserTopic}")
    private String topicName;

    @KafkaListener(topics = "${app.kafka.kafkaUserTopic}",
            groupId = "${app.kafka.kafkaGroupId}",
            containerFactory = "kafkaUserListenerContainerFactory")
    public void listen(@Payload UserEvent message,
                       @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) UUID key,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                       @Header(KafkaHeaders.RECEIVED_TIMESTAMP) Long timestamp) {
        userEventRepository.save(message);

        log.info("Received message: {}", message);
    }
}
