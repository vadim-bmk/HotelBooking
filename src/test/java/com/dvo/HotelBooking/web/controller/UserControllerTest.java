package com.dvo.HotelBooking.web.controller;

import com.dvo.HotelBooking.configuration.SecurityConfiguration;
import com.dvo.HotelBooking.entity.RoleType;
import com.dvo.HotelBooking.entity.User;
import com.dvo.HotelBooking.mapper.UserMapper;
import com.dvo.HotelBooking.security.UserDetailsServiceImpl;
import com.dvo.HotelBooking.service.UserService;
import com.dvo.HotelBooking.web.model.request.UpdateUserRequest;
import com.dvo.HotelBooking.web.model.request.UpsertUserRequest;
import com.dvo.HotelBooking.web.model.response.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({SecurityConfiguration.class, UserDetailsServiceImpl.class})
public class UserControllerTest {
    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private UserResponse userResponse;
    private final String URL = "/api/users";

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("user")
                .email("email@mail.ru")
                .password("12345")
                .roleType(RoleType.ROLE_USER)
                .build();

        userResponse = UserResponse.builder()
                .username("user")
                .email("email@mail.ru")
                .role("ROLE_USER")
                .build();
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testFindAll_withAdmin() throws Exception {
        when(userService.findAll()).thenReturn(List.of(user));
        when(userMapper.userToResponse(user)).thenReturn(userResponse);

        mockMvc.perform(get(URL + "/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].username").value("user"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void testFindAll_withUser() throws Exception {
        when(userService.findAll()).thenReturn(List.of(user));
        when(userMapper.userToResponse(user)).thenReturn(userResponse);

        mockMvc.perform(get(URL + "/all"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testFindByUsername_withAdmin() throws Exception {
        when(userService.findByUsername("user")).thenReturn(user);
        when(userMapper.userToResponse(user)).thenReturn(userResponse);

        mockMvc.perform(get(URL + "/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.email").value("email@mail.ru"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void testFindByUsername_withUser() throws Exception {
        when(userService.findByUsername("user")).thenReturn(user);
        when(userMapper.userToResponse(user)).thenReturn(userResponse);

        mockMvc.perform(get(URL + "/user"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreate() throws Exception {
        UpsertUserRequest request = UpsertUserRequest.builder()
                .username("user")
                .email("email@mail.ru")
                .password("12345")
                .build();

        when(userMapper.requestToUser(request)).thenReturn(user);
        when(userService.create(any(User.class), any())).thenReturn(user);
        when(userMapper.userToResponse(user)).thenReturn(userResponse);

        mockMvc.perform(post(URL + "/create")
                        .param("roleType", "ROLE_USER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testUpdate_withAdmin() throws Exception {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("updated@mail.ru")
                .password("12345")
                .build();

        when(userService.update(request, "user")).thenReturn(user);
        when(userMapper.userToResponse(user)).thenReturn(userResponse);

        mockMvc.perform(put(URL + "/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void testUpdate_withUser() throws Exception {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("updated@mail.ru")
                .password("12345")
                .build();

        when(userService.update(request, "user")).thenReturn(user);
        when(userMapper.userToResponse(user)).thenReturn(userResponse);

        mockMvc.perform(put(URL + "/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testDeleteByUsername_withAdmin() throws Exception {
        mockMvc.perform(delete(URL + "/user"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void testDeleteByUsername_withUser() throws Exception {
        mockMvc.perform(delete(URL + "/user"))
                .andExpect(status().isForbidden());
    }
}
