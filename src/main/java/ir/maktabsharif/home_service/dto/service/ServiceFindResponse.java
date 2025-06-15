package ir.maktabsharif.home_service.dto.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceFindResponse {
    private Integer id;
    private String name;
    private Double basePrice;
    private String description;
    private Integer parentServiceId;
}
