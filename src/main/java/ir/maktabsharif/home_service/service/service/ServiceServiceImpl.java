package ir.maktabsharif.home_service.service.service;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.service.ServiceSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.DuplicateInfoException;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import ir.maktabsharif.home_service.mapper.service.ServiceMapper;
import ir.maktabsharif.home_service.model.service.Service;
import ir.maktabsharif.home_service.repository.service.ServiceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@org.springframework.stereotype.Service
@Transactional
public class ServiceServiceImpl extends BaseServiceImpl<Service, Integer, ServiceRepository, ServiceMapper> implements ServiceService {
    public ServiceServiceImpl(ServiceRepository repository, ServiceMapper serviceMapper) {
        super(repository, serviceMapper);
    }

    @Override
    public Service saveWithDTO(ServiceSaveUpdateRequest serviceSaveUpdateRequest) {
        if (existsByName(serviceSaveUpdateRequest.getName())) {
            throw new DuplicateInfoException("Service name already exists");
        }
        Service service = mapper.mapToEntity(serviceSaveUpdateRequest);
        if (serviceSaveUpdateRequest.getParentServiceId() != null) {
            service.setParentService(findById(serviceSaveUpdateRequest.getParentServiceId()));
        }
        service.setName(serviceSaveUpdateRequest.getName().toLowerCase());
        return save(service);
    }

    @Override
    public Service updateWithDTO(ServiceSaveUpdateRequest serviceSaveUpdateRequest) {
        if (existsByName(serviceSaveUpdateRequest.getName())) {
            throw new DuplicateInfoException("Service name already exists");
        }
        Service service = findById(serviceSaveUpdateRequest.getId());
        mapper.updateEntityWithDTO(serviceSaveUpdateRequest, service);
        if (serviceSaveUpdateRequest.getParentServiceId() != null) {
            service.setParentService(findById(serviceSaveUpdateRequest.getParentServiceId()));
        }
        service.setName(serviceSaveUpdateRequest.getName().toLowerCase());
        return save(service);
    }

    @Override
    public void updateDescription(Integer id, String description) {
        Service byId = findById(id);
        byId.setDescription(description);
        save(byId);
    }

    @Override
    public void updateBasePrice(Integer id, Double basePrice) {
        Service byId = findById(id);
        byId.setBasePrice(BigDecimal.valueOf(basePrice));
        save(byId);
    }

    @Override
    public Page<Service> findAllAndParentServiceIsNull(Pageable pageable) {
        Page<Service> byParentServiceIsNull = repository.findByParentServiceIsNull(pageable);
        if (byParentServiceIsNull.getContent().isEmpty()) {
            throw new NoElementFoundException();
        }
        return byParentServiceIsNull;
    }

    @Override
    public Page<Service> findAllAndParentServiceIsNotNullByParentService(Service parent,Pageable pageable) {
        Page<Service> byParentService = repository.findByParentService(parent, pageable);
        if (byParentService.getContent().isEmpty()) {
            throw new NoElementFoundException();
        }
        return byParentService;
    }

    @Override
    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }
}
