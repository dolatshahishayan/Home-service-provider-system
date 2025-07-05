package ir.maktabsharif.home_service.service.service;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.service.ServiceSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.DuplicateInfoException;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import ir.maktabsharif.home_service.mapper.service.ServiceMapper;
import ir.maktabsharif.home_service.model.service.Service;
import ir.maktabsharif.home_service.repository.service.ServiceRepository;
import jakarta.transaction.Transactional;

import java.util.List;

@org.springframework.stereotype.Service
@Transactional
public class ServiceServiceImpl extends BaseServiceImpl<Service, Integer, ServiceRepository, ServiceMapper> implements ServiceService {
    public ServiceServiceImpl(ServiceRepository repository, ServiceMapper mapper) {
        super(repository, mapper);
    }
    @Override
    public Service saveWithDTO(ServiceSaveUpdateRequest serviceSaveUpdateRequest) {
        if (existsByName(serviceSaveUpdateRequest.getName())) {
            throw new DuplicateInfoException("Service name already exists");
        }
        Service service = mapper.mapToEntity(serviceSaveUpdateRequest);
        if (serviceSaveUpdateRequest.getParentServiceId()!=null) {
            service.setParentService(findById(serviceSaveUpdateRequest.getParentServiceId()));
        }
        return save(service);
    }
    @Override
    public Service updateWithDTO(ServiceSaveUpdateRequest serviceSaveUpdateRequest) {
        if (existsByName(serviceSaveUpdateRequest.getName())) {
            throw new DuplicateInfoException("Service name already exists");
        }
        Service service = mapper.mapToEntity(serviceSaveUpdateRequest);
        if (serviceSaveUpdateRequest.getParentServiceId()!=null) {
            service.setParentService(findById(serviceSaveUpdateRequest.getParentServiceId()));
        }
        return save(service);
    }
    @Override
    public void updateDescription(Integer id,String description) {
        Service byId = findById(id);
        byId.setDescription(description);
        save(byId);
    }
    @Override
    public void updateBasePrice(Integer id,Double basePrice) {
        Service byId = findById(id);
        byId.setBasePrice(basePrice);
        save(byId);
    }

    @Override
    public List<Service> findAllAndParentServiceIsNull() {
        return repository.findAllAndParentServiceIsNull().orElseThrow(NoElementFoundException::new);

    }

    @Override
    public List<Service> findAllAndParentServiceIsNotNullByParentService(Service parent) {
        return repository.findAllAndParentServiceIsNotNullByParentService(parent).orElseThrow(NoElementFoundException::new);
    }

    @Override
    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }
}
