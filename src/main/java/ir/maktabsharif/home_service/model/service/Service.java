package ir.maktabsharif.home_service.model.service;

import ir.maktabsharif.home_service.base.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Service extends BaseEntity {

    @Column(unique = true)
    private String name;

    private BigDecimal basePrice;

    private String description;
    @ManyToOne
    private Service parentService;


}
