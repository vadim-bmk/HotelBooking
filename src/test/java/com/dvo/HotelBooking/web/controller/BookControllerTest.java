package com.dvo.HotelBooking.web.controller;

import com.dvo.HotelBooking.configuration.SecurityConfiguration;
import com.dvo.HotelBooking.entity.Booking;
import com.dvo.HotelBooking.entity.Hotel;
import com.dvo.HotelBooking.entity.Room;
import com.dvo.HotelBooking.entity.User;
import com.dvo.HotelBooking.mapper.BookingMapper;
import com.dvo.HotelBooking.security.UserDetailsServiceImpl;
import com.dvo.HotelBooking.service.BookingService;
import com.dvo.HotelBooking.service.UserService;
import com.dvo.HotelBooking.web.model.request.UpsertBookingRequest;
import com.dvo.HotelBooking.web.model.response.BookingResponse;
import com.dvo.HotelBooking.web.model.response.BookingShortResponse;
import com.dvo.HotelBooking.web.model.response.RoomResponse;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@Import({SecurityConfiguration.class, UserDetailsServiceImpl.class})
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private BookingMapper bookingMapper;

    @MockBean
    private UserService userService;

    private final String URL = "/api/bookings";
    private Booking booking;
    private BookingResponse bookingResponse;
    private BookingShortResponse bookingShortResponse;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(1L).username("name").build();
        UserResponse userResponse = UserResponse.builder().username("name").build();
        Room room = Room.builder().id(1L).name("name").hotel(new Hotel()).unavailableDates(new ArrayList<>()).build();
        RoomResponse roomResponse = RoomResponse.builder().id(1L).name("name").hotelId(1L).unavailableDates(new ArrayList<>()).build();
        booking = Booking.builder()
                .id(1L)
                .room(room)
                .user(user)
                .checkInDate(LocalDate.of(2025, 6, 10))
                .checkOutDate(LocalDate.of(2025, 6, 30))
                .build();

        bookingResponse = BookingResponse.builder()
                .id(1L)
                .room(roomResponse)
                .user(userResponse)
                .checkInDate(LocalDate.of(2025, 6, 10))
                .checkOutDate(LocalDate.of(2025, 6, 30))
                .build();

        bookingShortResponse = BookingShortResponse.builder()
                .id(1L)
                .roomNumber("number")
                .username("name")
                .checkInDate(LocalDate.of(2025, 6, 10))
                .checkOutDate(LocalDate.of(2025, 6, 30))
                .build();
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testFindAll_withAdmin() throws Exception {
        when(bookingService.findAll()).thenReturn(List.of(booking));
        when(bookingMapper.bookingToShortResponse(booking)).thenReturn(bookingShortResponse);

        mockMvc.perform(get(URL + "/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roomNumber").value("number"));

        verify(bookingService).findAll();
        verify(bookingMapper).bookingToShortResponse(booking);
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void testFindAll_withUser() throws Exception {
        when(bookingService.findAll()).thenReturn(List.of(booking));
        when(bookingMapper.bookingToShortResponse(booking)).thenReturn(bookingShortResponse);

        mockMvc.perform(get(URL + "/all"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testFindById_withAdmin() throws Exception {
        when(bookingService.findById(1L)).thenReturn(booking);
        when(bookingMapper.bookingToResponse(booking)).thenReturn(bookingResponse);

        mockMvc.perform(get(URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.room.name").value("name"));

        verify(bookingService).findById(1L);
        verify(bookingMapper).bookingToResponse(booking);
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void testFindById_withUser() throws Exception {
        when(bookingService.findById(1L)).thenReturn(booking);
        when(bookingMapper.bookingToResponse(booking)).thenReturn(bookingResponse);

        mockMvc.perform(get(URL + "/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void testCreateBooking() throws Exception {
        UpsertBookingRequest request = UpsertBookingRequest.builder()
                .roomId(1L)
                .username("name")
                .checkInDate(LocalDate.of(2025, 6, 10))
                .checkOutDate(LocalDate.of(2025, 6, 30))
                .build();

        when(bookingService.createBooking(request)).thenReturn(booking);
        when(bookingMapper.bookingToResponse(booking)).thenReturn(bookingResponse);

        mockMvc.perform(post(URL + "/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(bookingService).createBooking(request);
        verify(bookingMapper).bookingToResponse(booking);
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testDeleteById_withAdmin() throws Exception {
        doNothing().when(bookingService).deleteBooking(1L);

        mockMvc.perform(delete(URL + "/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void testDeleteById_withUser() throws Exception {
        doNothing().when(bookingService).deleteBooking(1L);

        mockMvc.perform(delete(URL + "/1"))
                .andExpect(status().isForbidden());
    }
}
