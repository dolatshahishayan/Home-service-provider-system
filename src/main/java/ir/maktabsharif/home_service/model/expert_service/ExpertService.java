package ir.maktabsharif.home_service.model.expert_service;

import ir.maktabsharif.home_service.base.model.BaseEntity;
import ir.maktabsharif.home_service.model.service.Service;
import ir.maktabsharif.home_service.model.user.Expert;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Table(name = "expert_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpertService{

    @EmbeddedId
    private ExpertServiceId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("expertId")
    private Expert expert;

    @ManyToOne
    @MapsId("serviceId")
    private Service service;


}

