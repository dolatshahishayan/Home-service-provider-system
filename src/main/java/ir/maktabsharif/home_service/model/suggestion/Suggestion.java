package ir.maktabsharif.home_service.model.suggestion;

import ir.maktabsharif.home_service.base.model.BaseEntity;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.user.Expert;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity

public class Suggestion {
    @Id
    @SequenceGenerator(name = "my_entity_seq_generator", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_entity_seq_generator")
    private Integer id;
    @ManyToOne
    private Expert expert;
    @ManyToOne
    private Order order;

    private String description;

    private LocalDateTime creationDate;

    private LocalDateTime startDate;

    private Double price;

    private Double workDuration;

    private Boolean accepted;

    public Suggestion() {
    }

    public Suggestion(Integer id, Expert expert, Order order, String description, LocalDateTime creationDate, LocalDateTime startDate, Double price, Double workDuration, Boolean accepted) {
        this.id = id;
        this.expert = expert;
        this.order = order;
        this.description = description;
        this.creationDate = creationDate;
        this.startDate = startDate;
        this.price = price;
        this.workDuration = workDuration;
        this.accepted = accepted;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Expert getExpert() {
        return expert;
    }

    public void setExpert(Expert expert) {
        this.expert = expert;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getWorkDuration() {
        return workDuration;
    }

    public void setWorkDuration(Double workDuration) {
        this.workDuration = workDuration;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }
}
