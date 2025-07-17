package ir.maktabsharif.home_service.service.expert_service;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.exception.ExpertAlreadyInServiceException;
import ir.maktabsharif.home_service.exception.NoExpertFoundWithServiceException;
import ir.maktabsharif.home_service.mapper.expert_service.ExpertServiceMapper;
import ir.maktabsharif.home_service.model.expert_service.ExpertService;
import ir.maktabsharif.home_service.model.expert_service.ExpertServiceId;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.repository.expert_service.ExpertServiceCriteriaRepository;
import ir.maktabsharif.home_service.repository.expert_service.ExpertServiceRepository;
import ir.maktabsharif.home_service.service.service.ServiceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ExpertServiceServiceImpl extends BaseServiceImpl<ExpertService, Integer, ExpertServiceRepository, ExpertServiceMapper> implements ExpertServiceService {
    protected final ir.maktabsharif.home_service.service.expert.ExpertService expertService;
    protected final ServiceService serviceService;
    protected final ExpertServiceCriteriaRepository expertServiceCriteriaRepository;

    public ExpertServiceServiceImpl(ExpertServiceRepository repository, ExpertServiceMapper expertServiceMapper, ir.maktabsharif.home_service.service.expert.ExpertService expertService, ServiceService serviceService, ExpertServiceCriteriaRepository expertServiceCriteriaRepository) {
        super(repository, expertServiceMapper);
        this.expertService = expertService;
        this.serviceService = serviceService;
        this.expertServiceCriteriaRepository = expertServiceCriteriaRepository;
    }

    @Override
    public void addExpertToService(Integer expertId, Integer serviceId) {
        if (existsByExpertIdAndServiceId(expertId, serviceId)) {
            throw new ExpertAlreadyInServiceException();
        }
        Expert expert = expertService.findById(expertId);

        ir.maktabsharif.home_service.model.service.Service service = serviceService.findById(serviceId);

        ExpertService expertService = new ExpertService();
        expertService.setId(new ExpertServiceId(expertId, serviceId));
        expertService.setExpert(expert);
        expertService.setService(service);
        save(expertService);
    }

    @Override
    public void removeExpertFromService(Integer expertId, Integer serviceId) {
        ExpertService expertService = findByExpertIdAndServiceId(expertId, serviceId);
        if (expertService == null) {
            throw new NoExpertFoundWithServiceException();
        }
        delete(expertService);
    }

    @Override
    public List<ExpertService> findByExpertId(Integer expertId) {
        List<ExpertService> byExpertId = repository.findByExpertId(expertId);
        if (byExpertId.isEmpty()) {
            throw new NoExpertFoundWithServiceException();
        }
        return byExpertId;

    }

    @Override
    public List<Integer> findExpertIdsByServiceIds(List<Integer> serviceIds) {
        return expertServiceCriteriaRepository.findExpertIdsByServiceIds(serviceIds);
    }


    @Override
    public ExpertService findByExpertIdAndServiceId(Integer expertId, Integer serviceId) {
        return repository.findByExpertIdAndServiceId(expertId, serviceId).orElseThrow(NoExpertFoundWithServiceException::new);

    }

    @Override
    public boolean existsByExpertIdAndServiceId(Integer expertId, Integer serviceId) {
        return repository.existsByExpertIdAndServiceId(expertId, serviceId);
    }
}
