package ir.maktabsharif.home_service.service.expert_service;

import ir.maktabsharif.home_service.exception.ExpertAlreadyInServiceException;
import ir.maktabsharif.home_service.exception.NoExpertFoundWithServiceException;
import ir.maktabsharif.home_service.model.expert_service.Expert_Service;
import ir.maktabsharif.home_service.model.service.Service;
import ir.maktabsharif.home_service.model.user.Expert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpertServiceServiceImplTest {

    @Mock
    private ir.maktabsharif.home_service.repository.expert_service.Expert_ServiceRepository repository;

    @Mock
    private ir.maktabsharif.home_service.service.expert.ExpertService expertService;

    @Mock
    private ir.maktabsharif.home_service.service.service.ServiceService serviceService;

    @InjectMocks
    private Expert_ServiceServiceImpl service;

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

        verify(repository).beginTransaction();
        verify(repository).save(any(Expert_Service.class));
        verify(repository).commitTransaction();
    }


    @Test
    void removeExpertFromService_shouldThrow_whenExpertServiceNotFound() {
        when(service.findByExpertIdAndServiceId(1, 2)).thenReturn(null);

        assertThrows(NoExpertFoundWithServiceException.class, () ->
                service.removeExpertFromService(1, 2)
        );
    }

    @Test
    void removeExpertFromService_shouldDelete_whenExpertServiceExists() {
        Expert_Service expertService = new Expert_Service();
        expertService.setId(10);

        when(service.findByExpertIdAndServiceId(1, 2)).thenReturn(expertService);

        service.removeExpertFromService(1, 2);

        verify(repository).beginTransaction();
        verify(repository).delete(10);
        verify(repository).commitTransaction();
    }


    @Test
    void findByExpertId_shouldThrow_whenListEmpty() {
        when(repository.findByExpertId(1)).thenReturn(List.of());

        assertThrows(NoExpertFoundWithServiceException.class, () ->
                service.findByExpertId(1)
        );
    }

    @Test
    void findByExpertId_shouldReturnList_whenExists() {
        List<Expert_Service> expertServices = List.of(new Expert_Service());

        when(repository.findByExpertId(1)).thenReturn(expertServices);

        List<Expert_Service> result = service.findByExpertId(1);

        assertEquals(1, result.size());
    }


    @Test
    void findByExpertIdAndServiceId_shouldReturnFromRepository() {
        Expert_Service expertService = new Expert_Service();

        when(repository.findByExpertIdAndServiceId(1, 2)).thenReturn(expertService);

        Expert_Service result = service.findByExpertIdAndServiceId(1, 2);

        assertSame(expertService, result);
    }


    @Test
    void existsByExpertIdAndServiceId_shouldReturnTrueOrFalse() {
        when(repository.existsByExpertIdAndServiceId(1, 2)).thenReturn(true);

        assertTrue(service.existsByExpertIdAndServiceId(1, 2));

        when(repository.existsByExpertIdAndServiceId(1, 2)).thenReturn(false);

        assertFalse(service.existsByExpertIdAndServiceId(1, 2));
    }
}
