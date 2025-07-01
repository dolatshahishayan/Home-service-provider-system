package ir.maktabsharif.home_service.service.service;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.service.ServiceSaveUpdateRequest;
import ir.maktabsharif.home_service.model.service.Service;

public interface ServiceService extends BaseService<Service> {
    boolean existsByName(String name);
    void saveWithDTO(ServiceSaveUpdateRequest serviceSaveUpdateRequest);
    void updateWithDTO(ServiceSaveUpdateRequest serviceSaveUpdateRequest);
    void updateDescription(Integer id,String description);
    void updateBasePrice(Integer id,Double basePrice);
}
