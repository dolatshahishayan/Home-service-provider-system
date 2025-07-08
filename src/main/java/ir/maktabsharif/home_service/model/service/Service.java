package ir.maktabsharif.home_service.model.service;

import ir.maktabsharif.home_service.base.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Service extends BaseEntity {
    @Column(unique = true)
    private String name;

    private Double basePrice;

    private String description;
    @ManyToOne
    private Service parentService;
}
