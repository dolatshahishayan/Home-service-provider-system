package ir.maktabsharif.home_service.dto.expert_service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Expert_ServiceFindResponse {
    private Integer id;
    private Integer expertId;
    private Integer subServiceId;
}
