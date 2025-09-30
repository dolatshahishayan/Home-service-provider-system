package ir.maktabsharif.home_service.util.specification;

import ir.maktabsharif.home_service.model.user.Expert;
import org.springframework.data.jpa.domain.Specification;

public class ExpertSpecification {

    public static Specification<Expert> nameContains(String name) {
        return (root, cq, cb) -> {
            if (name == null || name.isBlank()) return null;
            String pattern = "%" + name.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("firstName")), pattern),
                    cb.like(cb.lower(root.get("lastName")), pattern)
            );
        };
    }

    public static Specification<Expert> scoreBetween(Double min, Double max) {
        return (root, cq, cb) -> {
            if (min == null && max == null) return null;
            if (min != null && max != null)
                return cb.between(root.get("score"), min, max);
            else if (min != null)
                return cb.greaterThanOrEqualTo(root.get("score"), min);
            else
                return cb.lessThanOrEqualTo(root.get("score"), max);
        };
    }
}
