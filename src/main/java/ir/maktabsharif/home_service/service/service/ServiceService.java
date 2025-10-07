package ir.maktabsharif.home_service.service.service;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.service.ServiceSaveUpdateRequest;
import ir.maktabsharif.home_service.model.service.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceService extends BaseService<Service,Integer> {
    boolean existsByName(String name);
    Service saveWithDTO(ServiceSaveUpdateRequest serviceSaveUpdateRequest);
    Service updateWithDTO(ServiceSaveUpdateRequest serviceSaveUpdateRequest);
    void updateDescription(Integer id,String description);
    void updateBasePrice(Integer id,Double basePrice);
    Page<Service> findAllAndParentServiceIsNull(Pageable pageable);
    Page<Service> findAllAndParentServiceIsNotNullByParentService(Service parent,Pageable pageable);
    void deleteAll();

    void deleteByIdAndAllSubServices(Integer serviceId);
}
