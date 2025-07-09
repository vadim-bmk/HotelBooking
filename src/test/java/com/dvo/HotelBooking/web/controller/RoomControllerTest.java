package com.dvo.HotelBooking.web.controller;

import com.dvo.HotelBooking.configuration.SecurityConfiguration;
import com.dvo.HotelBooking.entity.Hotel;
import com.dvo.HotelBooking.entity.Room;
import com.dvo.HotelBooking.mapper.RoomMapper;
import com.dvo.HotelBooking.security.UserDetailsServiceImpl;
import com.dvo.HotelBooking.service.HotelService;
import com.dvo.HotelBooking.service.RoomService;
import com.dvo.HotelBooking.service.UserService;
import com.dvo.HotelBooking.web.model.filter.RoomFilter;
import com.dvo.HotelBooking.web.model.request.UpsertRoomRequest;
import com.dvo.HotelBooking.web.model.response.RoomResponse;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomController.class)
@Import({SecurityConfiguration.class, UserDetailsServiceImpl.class})
public class RoomControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RoomService roomService;

    @MockBean
    private RoomMapper roomMapper;

    @MockBean
    private HotelService hotelService;

    @MockBean
    private UserService userService;

    private final String URL = "/api/room";
    private Room room;
    private RoomResponse response;
    private Hotel hotel;

    @BeforeEach
    void setUp() {
        room = Room.builder()
                .id(1L)
                .name("room")
                .hotel(new Hotel())
                .cost(BigDecimal.valueOf(100.0))
                .description("description")
                .maxPeople(10)
                .number("number")
                .unavailableDates(new ArrayList<>())
                .build();

        response = RoomResponse.builder()
                .id(1L)
                .name("room")
                .hotelId(1L)
                .cost(BigDecimal.valueOf(100.0))
                .description("description")
                .maxPeople(10)
                .number("number")
                .unavailableDates(new ArrayList<>())
                .build();

        hotel = Hotel.builder()
                .title("title")
                .name("name")
                .id(1L)
                .build();
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void testFindAllByFilter() throws Exception {
        RoomFilter filter = new RoomFilter();
        filter.setPageNumber(0);
        filter.setPageSize(10);

        when(roomService.findAllByFilter(any(RoomFilter.class))).thenReturn(List.of(room));
        when(roomMapper.roomToResponse(room)).thenReturn(response);

        mockMvc.perform(get(URL)
                        .param("pageNumber", filter.getPageNumber().toString())
                        .param("pageSize", filter.getPageSize().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.data[0].description").value("description"))
                .andExpect(jsonPath("$.data[0].name").value("room"));

        verify(roomService).findAllByFilter(any(RoomFilter.class));
        verify(roomMapper).roomToResponse(room);
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void testFindById() throws Exception {
        when(roomService.findById(1L)).thenReturn(room);
        when(roomMapper.roomToResponse(room)).thenReturn(response);

        mockMvc.perform(get(URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("room"));

        verify(roomService).findById(1L);
        verify(roomMapper).roomToResponse(room);
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testCreate_withAdmin() throws Exception {
        UpsertRoomRequest request = new UpsertRoomRequest();
        request.setName("room");
        request.setCost(BigDecimal.valueOf(100.0));
        request.setNumber("number");
        request.setDescription("description");
        request.setHotelId(1L);
        request.setUnavailableDates(new ArrayList<>());
        request.setMaxPeople(10);

        when(hotelService.findById(1L)).thenReturn(hotel);
        when(roomMapper.requestToRoom(request, hotel)).thenReturn(room);
        when(roomService.create(room)).thenReturn(room);
        when(roomMapper.roomToResponse(room)).thenReturn(response);

        mockMvc.perform(post(URL + "/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(hotelService).findById(1L);
        verify(roomMapper).requestToRoom(request, hotel);
        verify(roomService).create(room);
        verify(roomMapper).roomToResponse(room);
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void testCreate_withUser() throws Exception {
        UpsertRoomRequest request = new UpsertRoomRequest();
        request.setName("room");
        request.setCost(BigDecimal.valueOf(100.0));
        request.setNumber("number");
        request.setDescription("description");
        request.setHotelId(1L);
        request.setUnavailableDates(new ArrayList<>());
        request.setMaxPeople(10);

        when(hotelService.findById(1L)).thenReturn(hotel);
        when(roomMapper.requestToRoom(request, hotel)).thenReturn(room);
        when(roomService.create(room)).thenReturn(room);
        when(roomMapper.roomToResponse(room)).thenReturn(response);

        mockMvc.perform(post(URL + "/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testUpdate_withAdmin() throws Exception {
        UpsertRoomRequest request = new UpsertRoomRequest();
        request.setName("room");
        request.setCost(BigDecimal.valueOf(100.0));
        request.setNumber("number");
        request.setDescription("description");
        request.setHotelId(1L);
        request.setUnavailableDates(new ArrayList<>());
        request.setMaxPeople(10);

        when(roomService.update(1L, request)).thenReturn(room);
        when(roomMapper.roomToResponse(room)).thenReturn(response);

        mockMvc.perform(put(URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(roomService).update(1L, request);
        verify(roomMapper).roomToResponse(room);
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void testUpdate_withUser() throws Exception {
        UpsertRoomRequest request = new UpsertRoomRequest();
        request.setName("room");
        request.setCost(BigDecimal.valueOf(100.0));
        request.setNumber("number");
        request.setDescription("description");
        request.setHotelId(1L);
        request.setUnavailableDates(new ArrayList<>());
        request.setMaxPeople(10);

        when(roomService.update(1L, request)).thenReturn(room);
        when(roomMapper.roomToResponse(room)).thenReturn(response);

        mockMvc.perform(put(URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testDeleteById_withAdmin() throws Exception {
        doNothing().when(roomService).deleteById(1L);

        mockMvc.perform(delete(URL + "/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void testDeleteById_withUser() throws Exception {
        doNothing().when(roomService).deleteById(1L);

        mockMvc.perform(delete(URL + "/1"))
                .andExpect(status().isForbidden());
    }
}
