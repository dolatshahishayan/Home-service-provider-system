package ir.maktabsharif.home_service.model.expert_service;

import ir.maktabsharif.home_service.base.model.BaseEntity;
import ir.maktabsharif.home_service.model.service.Service;
import ir.maktabsharif.home_service.model.user.Expert;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "expert_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpertService extends BaseEntity {
    @ManyToOne
    private Expert expert;
    @ManyToOne
    private Service service;
}
