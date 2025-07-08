package com.dvo.HotelBooking.repository;

import com.dvo.HotelBooking.entity.Hotel;
import com.dvo.HotelBooking.web.model.filter.HotelFilter;
import org.springframework.data.jpa.domain.Specification;

public interface HotelSpecification {
    static Specification<Hotel> withFilter(HotelFilter filter) {
        return Specification.where(byId(filter.getId()))
                .and(byName(filter.getName()))
                .and(byTitle(filter.getTitle()))
                .and(byCity(filter.getCity()))
                .and(byAddress(filter.getAddress()))
                .and(byDistance(filter.getDistance()))
                .and(byRating(filter.getRating()))
                .and(byNumberOfRating(filter.getNumberOfRating()));
    }

    static Specification<Hotel> byId(Long id) {
        return ((root, query, cr) -> {
            if (id == null) {
                return null;
            }
            return cr.equal(root.get(Hotel.Fields.id), id);
        });
    }

    static Specification<Hotel> byName(String name) {
        return ((root, query, cr) -> {
            if (name == null) {
                return null;
            }
            return cr.like(root.get(Hotel.Fields.name), "%" + name + "%");
        });
    }

    static Specification<Hotel> byTitle(String title) {
        return ((root, query, cr) -> {
            if (title == null) {
                return null;
            }
            return cr.like(root.get(Hotel.Fields.title), "%" + title + "%");
        });
    }

    static Specification<Hotel> byCity(String city) {
        return ((root, query, cr) -> {
            if (city == null) {
                return null;
            }
            return cr.like(root.get(Hotel.Fields.city), "%" + city + "%");
        });
    }

    static Specification<Hotel> byAddress(String address) {
        return ((root, query, cr) -> {
            if (address == null) {
                return null;
            }
            return cr.like(root.get(Hotel.Fields.address), "%" + address + "%");
        });
    }

    static Specification<Hotel> byDistance(Double distance) {
        return ((root, query, cr) -> {
            if (distance == null) {
                return null;
            }
            return cr.lessThanOrEqualTo(root.get(Hotel.Fields.distance), distance);
        });
    }

    static Specification<Hotel> byRating(Double rating) {
        return ((root, query, cr) -> {
            if (rating == null) {
                return null;
            }
            return cr.greaterThanOrEqualTo(root.get(Hotel.Fields.rating), rating);
        });
    }

    static Specification<Hotel> byNumberOfRating(Integer numberOfRating) {
        return ((root, query, cr) -> {
            if (numberOfRating == null) {
                return null;
            }
            return cr.greaterThanOrEqualTo(root.get(Hotel.Fields.numberOfRating), numberOfRating);
        });
    }

}
