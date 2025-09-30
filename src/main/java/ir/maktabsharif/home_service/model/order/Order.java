package ir.maktabsharif.home_service.model.order;

import ir.maktabsharif.home_service.base.model.BaseEntity;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.service.Service;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.model.user.Expert;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")

public class Order  {
    @Id
    @SequenceGenerator(name = "my_entity_seq_generator", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_entity_seq_generator")
    private Integer id;
    private String description;

    private Double proposedPrice;

    private LocalDateTime startDate;

    private String address;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @ManyToOne
    private Customer customer;

    @ManyToOne
    private Service service;

    @ManyToOne
    private Expert expert;

    private LocalDateTime creationDate;

    private Double finalPrice;

    public Order() {
    }

    public Order(Integer id, String description, Double proposedPrice, LocalDateTime startDate, String address, OrderStatus orderStatus, Customer customer, Service service, Expert expert, LocalDateTime creationDate, Double finalPrice) {
        this.id = id;
        this.description = description;
        this.proposedPrice = proposedPrice;
        this.startDate = startDate;
        this.address = address;
        this.orderStatus = orderStatus;
        this.customer = customer;
        this.service = service;
        this.expert = expert;
        this.creationDate = creationDate;
        this.finalPrice = finalPrice;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getProposedPrice() {
        return proposedPrice;
    }

    public void setProposedPrice(Double proposedPrice) {
        this.proposedPrice = proposedPrice;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Expert getExpert() {
        return expert;
    }

    public void setExpert(Expert expert) {
        this.expert = expert;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(Double finalPrice) {
        this.finalPrice = finalPrice;
    }
}
