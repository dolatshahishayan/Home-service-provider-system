package ir.maktabsharif.home_service.dto.order;

import java.time.LocalDateTime;


public class OrderSummaryDTO {
    private Integer id;
    private Integer serviceId;
    private Integer customerId;
    private LocalDateTime startDate;
    private String address;

    public OrderSummaryDTO() {
    }

    public OrderSummaryDTO(Integer id, Integer serviceId, Integer customerId, LocalDateTime startDate, String address) {
        this.id = id;
        this.serviceId = serviceId;
        this.customerId = customerId;
        this.startDate = startDate;
        this.address = address;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
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
}
