package ir.maktabsharif.home_service.service.service;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.service.ServiceSaveUpdateRequest;
import ir.maktabsharif.home_service.model.service.Service;

import java.util.List;

public interface ServiceService extends BaseService<Service,Integer> {
    boolean existsByName(String name);
    Service saveWithDTO(ServiceSaveUpdateRequest serviceSaveUpdateRequest);
    Service updateWithDTO(ServiceSaveUpdateRequest serviceSaveUpdateRequest);
    void updateDescription(Integer id,String description);
    void updateBasePrice(Integer id,Double basePrice);
    List<Service> findAllAndParentServiceIsNull();
    List<Service> findAllAndParentServiceIsNotNullByParentService(Service parent);
}
