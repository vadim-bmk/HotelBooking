package com.dvo.HotelBooking.service.impl;

import com.dvo.HotelBooking.entity.RoleType;
import com.dvo.HotelBooking.entity.User;
import com.dvo.HotelBooking.entity.kafka.UserEvent;
import com.dvo.HotelBooking.exception.EntityExistsException;
import com.dvo.HotelBooking.exception.EntityNotFoundException;
import com.dvo.HotelBooking.mapper.UserMapper;
import com.dvo.HotelBooking.repository.UserRepository;
import com.dvo.HotelBooking.service.UserService;
import com.dvo.HotelBooking.web.model.request.UpdateUserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Value("${app.kafka.kafkaUserTopic}")
    private String topicName;

    public List<User> findAll() {
        log.info("Call findAll in UserServiceImpl");

        return userRepository.findAll();
    }

    @Override
    public User findByUsername(String username) {
        log.info("Call findByUsername in UserServiceImpl with username: {}", username);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Пользователь с указанным логином {0} не найден", username)));
    }

    @Override
    public boolean existsByUsernameAndEmail(String username, String email) {
        log.info("Call existsByUsernameAndEmail in UserServiceImpl with username: {} and email: {}", username, email);

        return userRepository.existsByUsernameAndEmail(username, email);
    }

    @Override
    @Transactional
    public User create(User user, RoleType roleType) {
        log.info("Call create in UserServiceImpl by user: {}", user);

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new EntityExistsException(MessageFormat.format("Пользователь с указанным именем {0} уже существует", user.getUsername()));
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EntityExistsException(MessageFormat.format("Пользователь с указанным email {0} уже существует", user.getEmail()));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoleType(Objects.requireNonNullElse(roleType, RoleType.ROLE_USER));

        User savedUser = userRepository.save(user);

        UserEvent event = new UserEvent();
        event.setUserId(savedUser.getId());
        kafkaTemplate.send(topicName, event);

        return savedUser;
    }

    @Override
    @Transactional
    public User update(UpdateUserRequest user, String username) {
        log.info("Call update in UserServiceImpl for username: {} with user: {}", username, user);

        User existedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Пользователь с указанным логином {0} не найден", username)));

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EntityExistsException(MessageFormat.format("Пользователь с указанным email {0} уже существует", user.getEmail()));
        }

        userMapper.updateRequestToUser(user, existedUser);
        existedUser.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(existedUser);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Call deleteById in UserServiceImpl with ID: {}", id);

        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByUsername(String username) {
        log.info("Call deleteByUsername in UserServiceImpl with username: {}", username);

        userRepository.deleteByUsername(username);
    }
}
