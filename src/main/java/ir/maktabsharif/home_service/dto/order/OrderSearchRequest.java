package ir.maktabsharif.home_service.dto.order;

import ir.maktabsharif.home_service.model.enums.OrderStatus;

import java.time.LocalDateTime;



public class OrderSearchRequest {
    private Integer userId;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private OrderStatus status;
    private Integer serviceId;

    public OrderSearchRequest(Integer userId, LocalDateTime fromDate, LocalDateTime toDate, OrderStatus status, Integer serviceId) {
        this.userId = userId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.status = status;
        this.serviceId = serviceId;
    }

    public OrderSearchRequest() {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDateTime getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDateTime fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDateTime getToDate() {
        return toDate;
    }

    public void setToDate(LocalDateTime toDate) {
        this.toDate = toDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }
}
