package ir.maktabsharif.home_service.model.suggestion;

import ir.maktabsharif.home_service.base.model.BaseEntity;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.user.Expert;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Suggestion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Expert expert;
    @ManyToOne
    private Order order;

    private String description;

    private LocalDateTime creationDate;

    private LocalDateTime startDate;

    private BigDecimal price;

    private BigDecimal workDuration;

    private Boolean accepted;


}
