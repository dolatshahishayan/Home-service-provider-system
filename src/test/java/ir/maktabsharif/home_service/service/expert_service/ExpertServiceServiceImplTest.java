package ir.maktabsharif.home_service.service.expert_service;

import ir.maktabsharif.home_service.exception.ExpertAlreadyInServiceException;
import ir.maktabsharif.home_service.exception.NoExpertFoundWithServiceException;
import ir.maktabsharif.home_service.model.expert_service.ExpertService;
import ir.maktabsharif.home_service.model.service.Service;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.repository.expert_service.ExpertServiceCriteriaRepository;
import ir.maktabsharif.home_service.repository.expert_service.ExpertServiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpertServiceServiceImplTest {

    @Mock
    private ExpertServiceRepository repository;

    @Mock
    private ir.maktabsharif.home_service.service.expert.ExpertService expertService;

    @Mock
    private ir.maktabsharif.home_service.service.service.ServiceService serviceService;

    @Mock
    private ExpertServiceCriteriaRepository expertServiceCriteriaRepository;

    @InjectMocks
    private ExpertServiceServiceImpl service;

    @Test
    void addExpertToService_shouldThrow_whenExpertAlreadyExistsInService() {
        Integer expertId = 1;
        Integer serviceId = 2;

        when(repository.existsByExpertIdAndServiceId(expertId, serviceId)).thenReturn(true);

        assertThrows(ExpertAlreadyInServiceException.class, () ->
                service.addExpertToService(expertId, serviceId)
        );
    }

    @Test
    void addExpertToService_shouldSave_whenExpertNotExistsInService() {
        Integer expertId = 1;
        Integer serviceId = 2;

        Expert expert = new Expert();
        Service myService = new Service();

        when(repository.existsByExpertIdAndServiceId(expertId, serviceId)).thenReturn(false);
        when(expertService.findById(expertId)).thenReturn(expert);
        when(serviceService.findById(serviceId)).thenReturn(myService);

        service.addExpertToService(expertId, serviceId);

        verify(repository).save(any(ExpertService.class));
    }


    @Test
    void removeExpertFromService_shouldThrow_whenExpertServiceNotFound() {
        when(repository.findByExpertIdAndServiceId(1, 2)).thenReturn(Optional.empty());

        assertThrows(NoExpertFoundWithServiceException.class, () ->
                service.removeExpertFromService(1, 2)
        );
    }

    @Test
    void removeExpertFromService_shouldDelete_whenExpertServiceExists() {
        ExpertService expertService = new ExpertService();
        expertService.setId(10);

        when(repository.findByExpertIdAndServiceId(1, 2)).thenReturn(Optional.of(expertService));

        service.removeExpertFromService(1, 2);

        verify(repository).deleteById(10);
    }


    @Test
    void findByExpertId_shouldThrow_whenListEmpty() {
        when(repository.findByExpertId(1)).thenReturn(Collections.emptyList());

        assertThrows(NoExpertFoundWithServiceException.class, () ->
                service.findByExpertId(1)
        );
    }


    @Test
    void findByExpertId_shouldReturnList_whenExists() {
        List<ExpertService> expertServices = List.of(new ExpertService());

        when(repository.findByExpertId(1)).thenReturn(expertServices);

        List<ExpertService> result = service.findByExpertId(1);

        assertEquals(1, result.size());
    }


    @Test
    void findByExpertIdAndServiceId_shouldReturnFromRepository() {
        ExpertService expertService = new ExpertService();

        when(repository.findByExpertIdAndServiceId(1, 2)).thenReturn(Optional.of(expertService));

        ExpertService result = service.findByExpertIdAndServiceId(1, 2);

        assertSame(expertService, result);
    }

    @Test
    void findExpertIdsByServiceIds_ShouldReturnExpertIds() {
        List<Integer> serviceIds = List.of(1, 2, 3);
        List<Integer> expectedExpertIds = List.of(10, 20, 30);

        when(expertServiceCriteriaRepository.findExpertIdsByServiceIds(serviceIds)).thenReturn(expectedExpertIds);

        List<Integer> actualExpertIds = service.findExpertIdsByServiceIds(serviceIds);

        assertEquals(expectedExpertIds, actualExpertIds);

        verify(expertServiceCriteriaRepository).findExpertIdsByServiceIds(serviceIds);
    }

    @Test
    void existsByExpertIdAndServiceId_shouldReturnTrueOrFalse() {
        when(repository.existsByExpertIdAndServiceId(1, 2)).thenReturn(true);

        assertTrue(service.existsByExpertIdAndServiceId(1, 2));

        when(repository.existsByExpertIdAndServiceId(1, 2)).thenReturn(false);

        assertFalse(service.existsByExpertIdAndServiceId(1, 2));
    }
}
