package ir.maktabsharif.home_service.model.expert_subservice;

import ir.maktabsharif.home_service.base.model.BaseEntity;
import ir.maktabsharif.home_service.model.subservice.SubService;
import ir.maktabsharif.home_service.model.user.Expert;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Expert_SubService extends BaseEntity {
    @ManyToOne
    private Expert expert;
    @ManyToOne
    private SubService subService;
}
