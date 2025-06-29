package ir.maktabsharif.home_service.service.service;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.service.ServiceSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.DuplicateInfoException;
import ir.maktabsharif.home_service.mapper.service.ServiceMapper;
import ir.maktabsharif.home_service.model.service.Service;
import ir.maktabsharif.home_service.repository.service.ServiceRepository;
@org.springframework.stereotype.Service
public class ServiceServiceImpl extends BaseServiceImpl<Service, ServiceSaveUpdateRequest, ServiceRepository, ServiceMapper> implements ServiceService {
    public ServiceServiceImpl(ServiceRepository repository, ServiceMapper mapper) {
        super(repository, mapper);
    }
    @Override
    public void saveWithDTO(ServiceSaveUpdateRequest serviceSaveUpdateRequest) {
        if (existsByName(serviceSaveUpdateRequest.getName())) {
            throw new DuplicateInfoException("Service name already exists");
        }
        save(mapper.mapToEntity(serviceSaveUpdateRequest));
    }
    @Override
    public void updateDescription(Integer id,String description) {
        Service byId = findById(id);
        byId.setDescription(description);
        update(byId);
    }
    @Override
    public void updateBasePrice(Integer id,Double basePrice) {
        Service byId = findById(id);
        byId.setBasePrice(basePrice);
        update(byId);
    }

    @Override
    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }
}
