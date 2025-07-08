package com.dvo.HotelBooking.service.impl;

import com.dvo.HotelBooking.entity.Hotel;
import com.dvo.HotelBooking.exception.EntityNotFoundException;
import com.dvo.HotelBooking.repository.HotelRepository;
import com.dvo.HotelBooking.repository.HotelSpecification;
import com.dvo.HotelBooking.repository.RoomRepository;
import com.dvo.HotelBooking.service.HotelService;
import com.dvo.HotelBooking.web.model.filter.HotelFilter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    @Override
    public List<Hotel> findAll() {
        log.info("Call findAll in HotelServiceImpl");

        return hotelRepository.findAll();
    }

    @Override
    public List<Hotel> findAllByFilter(HotelFilter filter) {
        log.info("Call findAllByFilter in HotelServiceImpl with filter: {}", filter);

        return hotelRepository.findAll(
                HotelSpecification.withFilter(filter),
                PageRequest.of(filter.getPageNumber(), filter.getPageSize())
        ).getContent();
    }

    @Override
    public Page<Hotel> findAllByPagination(Pageable pageable) {
        return hotelRepository.findAll(pageable);
    }

    @Override
    public Hotel findById(Long id) {
        log.info("Call findById in HotelServiceImpl with ID: {}", id);

        return hotelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Отель с ID {0} не найден!", id)));
    }

    @Override
    @Transactional
    public Hotel create(Hotel hotel) {
        log.info("Call create in HotelServiceImpl with hotel: {}", hotel);

        return hotelRepository.save(hotel);
    }

    @Override
    @Transactional
    public Hotel update(Long id, Hotel hotel) {
        log.info("Call update in HotelServiceImpl with ID: {} and hotel: {}", id, hotel);

        Hotel existedHotel = hotelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Отель с ID {0} не найден!", id)));

        existedHotel.setName(hotel.getName());
        existedHotel.setTitle(hotel.getTitle());
        existedHotel.setCity(hotel.getCity());
        existedHotel.setAddress(hotel.getAddress());
        existedHotel.setDistance(hotel.getDistance());

        return hotelRepository.save(existedHotel);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Call deleteById in HotelServiceImpl with ID: {}", id);

        roomRepository.deleteAllByHotelId(id);
        hotelRepository.deleteById(id);
    }

    @Override
    public void setRating(Long id, Integer newMark) {
        log.info("Call setRating in HotelServiceImpl for Hotel ID: {} and mark: {}", id, newMark);

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Отель с ID {0} не найден!", id)));

        double rating = hotel.getRating() == null ? 0.0 : hotel.getRating();
        int numberOfRating = hotel.getNumberOfRating() == null ? 0 : hotel.getNumberOfRating();

        double totalRating = rating * numberOfRating;
        totalRating = totalRating + newMark;
        numberOfRating = numberOfRating + 1;
        rating = totalRating / numberOfRating;
        rating = Math.round(rating * 10.0) / 10.0;


        hotel.setRating(rating);
        hotel.setNumberOfRating(numberOfRating);

        hotelRepository.save(hotel);
    }
}
