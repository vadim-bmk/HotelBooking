package com.dvo.HotelBooking.web.controller;

import com.dvo.HotelBooking.entity.RoleType;
import com.dvo.HotelBooking.entity.User;
import com.dvo.HotelBooking.mapper.UserMapper;
import com.dvo.HotelBooking.service.UserService;
import com.dvo.HotelBooking.web.model.request.UpdateUserRequest;
import com.dvo.HotelBooking.web.model.request.UpsertUserRequest;
import com.dvo.HotelBooking.web.model.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get users",
            description = "Get all users",
            tags = {"users"}
    )
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll().stream().map(userMapper::userToResponse).toList());
    }

    @GetMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get user",
            description = "Get user by Username",
            tags = {"users"}
    )
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserResponse> findByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userMapper.userToResponse(userService.findByUsername(username)));
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create user",
            description = "Create new user",
            tags = {"users"}
    )
    public ResponseEntity<UserResponse> create(@RequestBody @Valid UpsertUserRequest request,
                                               @RequestParam RoleType roleType) {
        User newUser = userService.create(userMapper.requestToUser(request), roleType);

        return ResponseEntity.ok(userMapper.userToResponse(newUser));
    }

    @PutMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Update user",
            description = "Update email and password for user",
            tags = {"users"}
    )
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserResponse> update(@RequestBody @Valid UpdateUserRequest request,
                                               @PathVariable String username) {
        User updatedUser = userService.update(request, username);

        return ResponseEntity.ok(userMapper.userToResponse(updatedUser));
    }

    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete user",
            description = "Delete user by username",
            tags = {"users"}
    )
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteByUsername(@PathVariable String username) {
        userService.deleteByUsername(username);
    }

}
