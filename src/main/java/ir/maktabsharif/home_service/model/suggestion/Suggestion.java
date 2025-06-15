package ir.maktabsharif.home_service.model.suggestion;

import ir.maktabsharif.home_service.base.model.BaseEntity;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.user.Expert;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Suggestion extends BaseEntity {
    @ManyToOne
    private Expert expert;
    @ManyToOne
    private Order order;

    private String description;

    private LocalDate registerDate;

    private Double price;

    private Double workDuration;
}
