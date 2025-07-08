package com.dvo.HotelBooking.service.impl;

import com.dvo.HotelBooking.entity.Hotel;
import com.dvo.HotelBooking.entity.Room;
import com.dvo.HotelBooking.exception.EntityNotFoundException;
import com.dvo.HotelBooking.mapper.RoomMapper;
import com.dvo.HotelBooking.repository.HotelRepository;
import com.dvo.HotelBooking.repository.RoomRepository;
import com.dvo.HotelBooking.repository.RoomSpecification;
import com.dvo.HotelBooking.web.model.filter.RoomFilter;
import com.dvo.HotelBooking.web.model.request.UpsertRoomRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceImplTest {
    @InjectMocks
    private RoomServiceImpl roomService;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private RoomMapper roomMapper;

    @Test
    void testFindAll() {
        when(roomRepository.findAll()).thenReturn(List.of(new Room()));

        List<Room> result = roomService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testFindAllByFilter() {
        RoomFilter filter = new RoomFilter();
        filter.setPageNumber(0);
        filter.setPageSize(10);
        filter.setName("name");

        List<Room> rooms = List.of(new Room(), new Room());
        Page<Room> page = new PageImpl<>(rooms, PageRequest.of(filter.getPageNumber(), filter.getPageSize()), rooms.size());

        when(roomRepository.findAll(
                any((Class<Specification<Room>>) (Class<?>) Specification.class),
                eq(PageRequest.of(filter.getPageNumber(), filter.getPageSize()))
        )).thenReturn(page);

        List<Room> result = roomService.findAllByFilter(filter);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(rooms, result);
    }

    @Test
    void testFindById() {
        Room room = Room.builder().id(1L).name("name").build();
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        Room result = roomService.findById(1L);

        assertNotNull(result);
        assertEquals(room, result);
    }

    @Test
    void testFindById_whenNotFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> roomService.findById(1L));
    }

    @Test
    void testCreate(){
        Room room = Room.builder().name("name").build();
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        Room result = roomService.create(room);
        assertNotNull(result);
        assertEquals(room.getName(), result.getName());
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    void testUpdate() {
        Room existed = Room.builder().name("Room 1").build();
        Hotel hotel = Hotel.builder().name("Hotel 1").build();

        UpsertRoomRequest request = new UpsertRoomRequest();
        request.setName("Room updated");
        request.setHotelId(1L);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(existed));
        doAnswer(invocationOnMock -> {
            existed.setName(request.getName());
            existed.setHotel(hotel);
            return null;
        }).when(roomMapper).updateRoomFromRequest(request, existed);

        when(roomRepository.save(existed)).thenReturn(existed);

        Room actual = roomService.update(1L, request);

        assertEquals("Room updated", actual.getName());
        assertEquals(hotel, actual.getHotel());
        verify(roomMapper).updateRoomFromRequest(request, existed);
        verify(roomRepository).save(existed);
    }

    @Test
    void testDeleteById() {
        roomService.deleteById(1L);
        verify(roomRepository, times(1)).deleteById(1L);
    }

}
