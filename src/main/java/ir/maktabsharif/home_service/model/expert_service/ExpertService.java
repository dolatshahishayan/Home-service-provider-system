package ir.maktabsharif.home_service.model.expert_service;

import ir.maktabsharif.home_service.model.service.Service;
import ir.maktabsharif.home_service.model.user.Expert;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "expert_service")

@NoArgsConstructor
@AllArgsConstructor
public class ExpertService{

    @EmbeddedId
    private ExpertServiceId id;

    @ManyToOne
    @MapsId("expertId")
    private Expert expert;

    @ManyToOne
    @MapsId("serviceId")
    private Service service;

    public ExpertServiceId getId() {
        return id;
    }

    public void setId(ExpertServiceId id) {
        this.id = id;
    }

    public Expert getExpert() {
        return expert;
    }

    public void setExpert(Expert expert) {
        this.expert = expert;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}

