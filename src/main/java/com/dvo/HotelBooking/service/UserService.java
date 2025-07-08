package com.dvo.HotelBooking.service;

import com.dvo.HotelBooking.entity.RoleType;
import com.dvo.HotelBooking.entity.User;
import com.dvo.HotelBooking.web.model.request.UpdateUserRequest;

import java.util.List;

public interface UserService {
    List<User> findAll();

    User findByUsername(String username);

    boolean existsByUsernameAndEmail(String username, String email);

    User create(User user, RoleType roleType);

    User update(UpdateUserRequest request, String username);

    void deleteById(Long id);

    void deleteByUsername(String username);
}
