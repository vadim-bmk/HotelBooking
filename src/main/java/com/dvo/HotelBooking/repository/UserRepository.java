package com.dvo.HotelBooking.repository;

import com.dvo.HotelBooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsernameAndEmail(String username, String email);

    void deleteByUsername(String username);

    boolean existsByEmail(String email);
}
