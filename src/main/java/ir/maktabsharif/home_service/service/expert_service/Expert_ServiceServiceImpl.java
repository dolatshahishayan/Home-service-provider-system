package ir.maktabsharif.home_service.service.expert_service;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.expert_service.Expert_ServiceSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.ExpertAlreadyInServiceException;
import ir.maktabsharif.home_service.exception.NoExpertFoundWithServiceException;
import ir.maktabsharif.home_service.mapper.expert_service.Expert_ServiceMapper;
import ir.maktabsharif.home_service.model.expert_service.Expert_Service;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.repository.expert_service.Expert_ServiceRepository;
import ir.maktabsharif.home_service.service.expert.ExpertService;
import ir.maktabsharif.home_service.service.service.ServiceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Expert_ServiceServiceImpl extends BaseServiceImpl<Expert_Service, Expert_ServiceSaveUpdateRequest, Expert_ServiceRepository, Expert_ServiceMapper> implements Expert_ServiceService {
    protected final ExpertService expertService;
    protected final ServiceService serviceService;
    public Expert_ServiceServiceImpl(Expert_ServiceRepository repository, Expert_ServiceMapper mapper, ExpertService expertService, ServiceService serviceService) {
        super(repository, mapper);
        this.expertService = expertService;
        this.serviceService = serviceService;
    }

    @Override
    public void addExpertToService(Integer expertId, Integer serviceId) {
        if(existsByExpertIdAndServiceId(expertId, serviceId)) {
            throw new ExpertAlreadyInServiceException();
        }
        Expert expert = expertService.findById(expertId);

        ir.maktabsharif.home_service.model.service.Service service = serviceService.findById(serviceId);

        Expert_Service expertService = new Expert_Service();
        expertService.setExpert(expert);
        expertService.setService(service);
        save(expertService);
    }
    @Override
    public void removeExpertFromService(Integer expertId, Integer serviceId) {
        Expert_Service expertService = findByExpertIdAndServiceId(expertId, serviceId);
        if (expertService == null) {
            throw new NoExpertFoundWithServiceException();
        }
        delete(expertService.getId());
    }

    @Override
    public List<Expert_Service> findByExpertId(Integer expertId) {
        List<Expert_Service> byExpertId = repository.findByExpertId(expertId);
        if (byExpertId.isEmpty()) {
            throw new NoExpertFoundWithServiceException();
        }
        return byExpertId;
    }

    @Override
    public Expert_Service findByExpertIdAndServiceId(Integer expertId, Integer serviceId) {
        return repository.findByExpertIdAndServiceId(expertId, serviceId);
    }

    @Override
    public boolean existsByExpertIdAndServiceId(Integer expertId, Integer serviceId) {
        return repository.existsByExpertIdAndServiceId(expertId, serviceId);
    }
}
