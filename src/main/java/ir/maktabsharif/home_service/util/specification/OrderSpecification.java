package ir.maktabsharif.home_service.util.specification;

import ir.maktabsharif.home_service.dto.order.OrderSearchRequest;
import ir.maktabsharif.home_service.model.order.Order;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {
    public static Specification<Order> buildSearchSpec(OrderSearchRequest request) {
        return (root, _, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (request.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), request.getFromDate()));
            }

            if (request.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startDate"), request.getToDate()));
            }

            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("orderStatus"), request.getStatus()));
            }

            if (request.getServiceId() != null) {
                predicates.add(cb.equal(root.get("service").get("id"), request.getServiceId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
