package ir.maktabsharif.home_service.model.service;

import ir.maktabsharif.home_service.base.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity


public class Service {
    @Id
    @SequenceGenerator(name = "my_entity_seq_generator", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_entity_seq_generator")
    private Integer id;
    @Column(unique = true)
    private String name;

    private Double basePrice;

    private String description;
    @ManyToOne
    private Service parentService;

    public Service() {
    }

    public Service(Integer id, String name, Double basePrice, String description, Service parentService) {
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
        this.description = description;
        this.parentService = parentService;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Service getParentService() {
        return parentService;
    }

    public void setParentService(Service parentService) {
        this.parentService = parentService;
    }
}
