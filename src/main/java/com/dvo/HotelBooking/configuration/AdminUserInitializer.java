package com.dvo.HotelBooking.configuration;

import com.dvo.HotelBooking.entity.RoleType;
import com.dvo.HotelBooking.entity.User;
import com.dvo.HotelBooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Configuration
public class AdminUserInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()){
            User admin = User.builder()
                    .username("admin")
                    .email("admin@mail.ru")
                    .password(passwordEncoder.encode("12345"))
                    .roleType(RoleType.ROLE_ADMIN)
                    .build();

            userRepository.save(admin);
        }
    }
}
