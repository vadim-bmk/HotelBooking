package com.dvo.HotelBooking.service;

import com.dvo.HotelBooking.entity.Hotel;
import com.dvo.HotelBooking.web.model.filter.HotelFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HotelService {
    List<Hotel> findAll();

    List<Hotel> findAllByFilter(HotelFilter filter);

    Page<Hotel> findAllByPagination(Pageable pageable);

    Hotel findById(Long id);

    Hotel create(Hotel hotel);

    Hotel update(Long id, Hotel hotel);

    void deleteById(Long id);

    void setRating(Long id, Integer newMark);
}
