package com.dvo.HotelBooking.web.controller;

import com.dvo.HotelBooking.configuration.SecurityConfiguration;
import com.dvo.HotelBooking.entity.Hotel;
import com.dvo.HotelBooking.mapper.HotelMapper;
import com.dvo.HotelBooking.security.UserDetailsServiceImpl;
import com.dvo.HotelBooking.service.HotelService;
import com.dvo.HotelBooking.service.UserService;
import com.dvo.HotelBooking.web.model.filter.HotelFilter;
import com.dvo.HotelBooking.web.model.request.UpdateHotelRequest;
import com.dvo.HotelBooking.web.model.request.UpsertHotelRequest;
import com.dvo.HotelBooking.web.model.response.HotelResponse;
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

@WebMvcTest(HotelController.class)
@Import({SecurityConfiguration.class, UserDetailsServiceImpl.class})
public class HotelControllerTest {
    @MockBean
    private HotelService hotelService;

    @MockBean
    private HotelMapper hotelMapper;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private final String URL = "/api/hotel";
    private Hotel hotel;
    private HotelResponse hotelResponse;

    @BeforeEach
    void setUp() {
        hotel = Hotel.builder()
                .title("title")
                .name("name")
                .id(1L)
                .build();

        hotelResponse = HotelResponse.builder()
                .title("title")
                .name("name")
                .id(1L)
                .build();


    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void testFindAllByFilter() throws Exception {
        HotelFilter filter = new HotelFilter();
        filter.setPageNumber(0);
        filter.setPageSize(10);

        when(hotelService.findAllByFilter(filter)).thenReturn(List.of(hotel));
        when(hotelMapper.hotelToResponse(hotel)).thenReturn(hotelResponse);

        mockMvc.perform(get(URL)
                        .param("pageNumber", filter.getPageNumber().toString())
                        .param("pageSize", filter.getPageSize().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.data[0].title").value("title"));

        verify(hotelService).findAllByFilter(any(HotelFilter.class));
        verify(hotelMapper).hotelToResponse(hotel);
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void testFindById() throws Exception {
        when(hotelService.findById(1L)).thenReturn(hotel);
        when(hotelMapper.hotelToResponse(hotel)).thenReturn(hotelResponse);

        mockMvc.perform(get(URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.name").value("name"));

        verify(hotelService).findById(1L);
        verify(hotelMapper).hotelToResponse(hotel);
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testCreateHotel_withAdmin() throws Exception {
        UpsertHotelRequest request = UpsertHotelRequest.builder()
                .title("title")
                .name("name")
                .city("city")
                .address("address")
                .distance(10.0)
                .build();

        when(hotelMapper.requestToHotel(request)).thenReturn(hotel);
        when(hotelService.create(hotel)).thenReturn(hotel);
        when(hotelMapper.hotelToResponse(hotel)).thenReturn(hotelResponse);

        mockMvc.perform(post(URL + "/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(hotelMapper).requestToHotel(request);
        verify(hotelService).create(hotel);
        verify(hotelMapper).hotelToResponse(hotel);
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void testCreateHotel_withUser() throws Exception {
        UpsertHotelRequest request = UpsertHotelRequest.builder()
                .title("title")
                .name("name")
                .city("city")
                .address("address")
                .distance(10.0)
                .build();

        when(hotelMapper.requestToHotel(request)).thenReturn(hotel);
        when(hotelService.create(hotel)).thenReturn(hotel);
        when(hotelMapper.hotelToResponse(hotel)).thenReturn(hotelResponse);

        mockMvc.perform(post(URL + "/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testUpdateHotel_withAdmin() throws Exception {
        UpdateHotelRequest request = UpdateHotelRequest.builder()
                .title("title")
                .name("name")
                .city("city")
                .address("address")
                .distance(10.0)
                .build();

        when(hotelService.findById(1L)).thenReturn(hotel);
        when(hotelService.update(1L, hotel)).thenReturn(hotel);
        when(hotelMapper.hotelToResponse(hotel)).thenReturn(hotelResponse);

        mockMvc.perform(put(URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(hotelService).findById(1L);
        verify(hotelService).update(1L, hotel);
        verify(hotelMapper).hotelToResponse(hotel);
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void testUpdateHotel_withUser() throws Exception {
        UpdateHotelRequest request = UpdateHotelRequest.builder()
                .title("title")
                .name("name")
                .city("city")
                .address("address")
                .distance(10.0)
                .build();

        when(hotelService.findById(1L)).thenReturn(hotel);
        when(hotelService.update(1L, hotel)).thenReturn(hotel);
        when(hotelMapper.hotelToResponse(hotel)).thenReturn(hotelResponse);

        mockMvc.perform(put(URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testDeleteById_withAdmin() throws Exception {
        doNothing().when(hotelService).deleteById(1L);

        mockMvc.perform(delete(URL + "/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void testDeleteById_withUser() throws Exception {
        doNothing().when(hotelService).deleteById(1L);

        mockMvc.perform(delete(URL + "/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void testSetRating() throws Exception {
        doNothing().when(hotelService).setRating(1L, 4);

        mockMvc.perform(put(URL + "/1/rating")
                        .param("mark", "4"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void testSetRating_withIllegalMark() throws Exception {
        doNothing().when(hotelService).setRating(1L, 6);

        mockMvc.perform(put(URL + "/1/rating")
                        .param("mark", "6"))
                .andExpect(status().isBadRequest());
    }
}
