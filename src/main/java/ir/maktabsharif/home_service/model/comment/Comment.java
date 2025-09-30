package ir.maktabsharif.home_service.model.comment;

import ir.maktabsharif.home_service.model.order.Order;


import javax.persistence.*;
import java.time.LocalDateTime;
@Entity

public class Comment{

    @Id
    @SequenceGenerator(name = "my_entity_seq_generator", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_entity_seq_generator")
    private Integer id;

    private String context;

    private Double expertScore;

    @OneToOne
    private Order order;

    private LocalDateTime registrationDate;

    public Comment() {
    }

    public Comment(Integer id, String context, Double expertScore, Order order, LocalDateTime registrationDate) {
        this.id = id;
        this.context = context;
        this.expertScore = expertScore;
        this.order = order;
        this.registrationDate = registrationDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Double getExpertScore() {
        return expertScore;
    }

    public void setExpertScore(Double expertScore) {
        this.expertScore = expertScore;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
}
