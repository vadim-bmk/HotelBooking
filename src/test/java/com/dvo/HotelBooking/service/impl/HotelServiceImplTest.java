package com.dvo.HotelBooking.service.impl;

import com.dvo.HotelBooking.entity.Hotel;
import com.dvo.HotelBooking.exception.EntityNotFoundException;
import com.dvo.HotelBooking.repository.HotelRepository;
import com.dvo.HotelBooking.repository.RoomRepository;
import com.dvo.HotelBooking.web.model.filter.HotelFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class HotelServiceImplTest {
    @InjectMocks
    private HotelServiceImpl hotelService;

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private RoomRepository roomRepository;

    @Test
    void testFindAll() {
        when(hotelRepository.findAll()).thenReturn(List.of(new Hotel()));

        List<Hotel> result = hotelService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(hotelRepository, times(1)).findAll();
    }

    @Test
    void testFindAllByFilter() {
        HotelFilter filter = new HotelFilter();
        filter.setPageNumber(0);
        filter.setPageSize(10);
        filter.setName("name");

        List<Hotel> hotels = List.of(new Hotel(), new Hotel());
        Page<Hotel> page = new PageImpl<>(hotels, PageRequest.of(filter.getPageNumber(), filter.getPageSize()), hotels.size());


        when(hotelRepository.findAll(
                any((Class<Specification<Hotel>>) (Class<?>) Specification.class),
                eq(PageRequest.of(filter.getPageNumber(), filter.getPageSize())))
        ).thenReturn(page);

        List<Hotel> result = hotelService.findAllByFilter(filter);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(hotels, result);
    }

    @Test
    void testFindAllByPagination() {
        when(hotelRepository.findAll(
                PageRequest.of(0, 10)
        )).thenReturn(new PageImpl<>(List.of(new Hotel(), new Hotel())));

        Page<Hotel> result = hotelService.findAllByPagination(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
    }

    @Test
    void testFindById() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(new Hotel()));
        Hotel result = hotelService.findById(1L);

        assertNotNull(result);
        verify(hotelRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_whenNotFound() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> hotelService.findById(1L));
    }

    @Test
    void testCreate() {
        Hotel hotel = Hotel.builder().name("name").title("title").build();
        when(hotelRepository.save(any(Hotel.class))).thenReturn(hotel);

        Hotel result = hotelService.create(hotel);

        assertNotNull(result);
        assertEquals(hotel, result);
        verify(hotelRepository, times(1)).save(hotel);
    }

    @Test
    void testUpdate() {
        Hotel existed = Hotel.builder().id(1L).name("name").build();

        Hotel updated = Hotel.builder().id(1L).name("Hotel update").build();

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(existed));
        when(hotelRepository.save(existed)).thenReturn(existed);

        Hotel actual = hotelService.update(1L, updated);

        assertEquals("Hotel update", actual.getName());
        verify(hotelRepository).findById(1L);
        verify(hotelRepository).save(existed);
    }

    @Test
    void testUpdate_whenNotFound() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> hotelService.update(1L, new Hotel()));
    }

    @Test
    void testDeleteById() {
        hotelService.deleteById(1L);

        verify(roomRepository, times(1)).deleteAllByHotelId(1L);
        verify(hotelRepository, times(1)).deleteById(1L);
    }


}
