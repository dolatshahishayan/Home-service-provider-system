package ir.maktabsharif.home_service.model.expert_service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ExpertServiceId implements Serializable {
    private Integer expertId;
    private Integer serviceId;


}
