package ir.maktabsharif.home_service.dto.service;

public class ServiceFindResponse {
    private Integer id;
    private String name;
    private Double basePrice;
    private String description;
    private Integer parentServiceId;

    public ServiceFindResponse() {
    }

    public ServiceFindResponse(Integer id, String name, Double basePrice, String description, Integer parentServiceId) {
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
        this.description = description;
        this.parentServiceId = parentServiceId;
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

    public Integer getParentServiceId() {
        return parentServiceId;
    }

    public void setParentServiceId(Integer parentServiceId) {
        this.parentServiceId = parentServiceId;
    }
}
