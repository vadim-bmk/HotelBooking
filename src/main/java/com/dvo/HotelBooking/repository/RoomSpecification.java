package com.dvo.HotelBooking.repository;

import com.dvo.HotelBooking.entity.Hotel;
import com.dvo.HotelBooking.entity.Room;
import com.dvo.HotelBooking.web.model.filter.RoomFilter;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface RoomSpecification {

    static Specification<Room> withFilter(RoomFilter filter) {
        return Specification.where(byId(filter.getId()))
                .and(byName(filter.getName()))
                .and(byDescription(filter.getDescription()))
                .and(byNumber(filter.getNumber()))
                .and(byCost(filter.getMinCost(), filter.getMaxCost()))
                .and(byMaxPeople(filter.getMaxPeople()))
                .and(byUnavailableDates(filter.getCheckInDate(), filter.getCheckOutDate()))
                .and(byHotelId(filter.getHotelId()));
    }

    static Specification<Room> byId(Long id) {
        return (((root, query, criteriaBuilder) -> {
            if (id == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get(Room.Fields.id), id);
        }));
    }

    static Specification<Room> byName(String name) {
        return ((root, query, criteriaBuilder) -> {
            if (name == null) {
                return null;
            }
            return criteriaBuilder.like(root.get(Room.Fields.name), "%" + name + "%");
        });
    }

    static Specification<Room> byDescription(String description) {
        return ((root, query, criteriaBuilder) -> {
            if (description == null) {
                return null;
            }
            return criteriaBuilder.like(root.get(Room.Fields.description), "%" + description + "%");
        });
    }

    static Specification<Room> byNumber(String number) {
        return ((root, query, criteriaBuilder) -> {
            if (number == null) {
                return null;
            }
            return criteriaBuilder.like(root.get(Room.Fields.number), "%" + number + "%");
        });
    }

    static Specification<Room> byCost(BigDecimal minCost, BigDecimal maxCost) {
        return ((root, query, criteriaBuilder) -> {
            if (minCost == null && maxCost == null) {
                return null;
            }
            if (minCost == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get(Room.Fields.cost), maxCost);
            }
            if (maxCost == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(Room.Fields.cost), minCost);
            }

            return criteriaBuilder.between(root.get(Room.Fields.cost), minCost, maxCost);
        });
    }

    static Specification<Room> byMaxPeople(Integer maxPeople) {
        return ((root, query, criteriaBuilder) -> {
            if (maxPeople == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get(Room.Fields.maxPeople), maxPeople);
        });
    }

    static Specification<Room> byUnavailableDates(LocalDate checkInDate, LocalDate checkOutDate) {
        return ((root, query, criteriaBuilder) -> {
            if (checkInDate == null && checkOutDate == null) {
                return null;
            }

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Room> subRoot = subquery.from(Room.class);
            Join<Room, LocalDate> unavailable = subRoot.join("unavailableDates");

            subquery.select(subRoot.get("id"))
                    .where(
                            criteriaBuilder.equal(subRoot.get("id"), root.get("id")),
                            unavailable.in(generateDateRange(checkInDate, checkOutDate))
                    );

            return criteriaBuilder.not(criteriaBuilder.exists(subquery));
        });
    }

    static Specification<Room> byHotelId(Long hotelId) {
        return ((root, query, criteriaBuilder) -> {
            if (hotelId == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get(Room.Fields.hotel).get(Hotel.Fields.id), hotelId);
        });
    }

    private static List<LocalDate> generateDateRange(LocalDate start, LocalDate end) {
        return start.datesUntil(end.plusDays(1)).toList();
    }
}
