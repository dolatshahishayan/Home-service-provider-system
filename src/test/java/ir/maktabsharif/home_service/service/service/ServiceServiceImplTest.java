package ir.maktabsharif.home_service.service.service;

import ir.maktabsharif.home_service.dto.service.ServiceSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.DuplicateInfoException;
import ir.maktabsharif.home_service.mapper.service.ServiceMapper;
import ir.maktabsharif.home_service.model.service.Service;
import ir.maktabsharif.home_service.repository.service.ServiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceServiceImplTest {

    @Mock
    private ServiceRepository repository;

    @Mock
    private ServiceMapper mapper;

    @InjectMocks
    private ServiceServiceImpl service;


    @Test
    void saveWithDTO_shouldThrow_whenNameAlreadyExists() {
        ServiceSaveUpdateRequest dto = new ServiceSaveUpdateRequest();
        dto.setName("Cleaning");

        when(repository.existsByName("Cleaning")).thenReturn(true);

        assertThrows(DuplicateInfoException.class, () -> service.saveWithDTO(dto));
    }

    @Test
    void saveWithDTO_shouldSave_whenNameUnique() {
        ServiceSaveUpdateRequest dto = new ServiceSaveUpdateRequest();
        dto.setName("Plumbing");

        Service model = new Service();

        when(repository.existsByName("Plumbing")).thenReturn(false);
        when(mapper.mapToEntity(dto)).thenReturn(model);

        service.saveWithDTO(dto);

        verify(repository).beginTransaction();
        verify(repository).save(model);
        verify(repository).commitTransaction();
    }


    @Test
    void updateDescription_shouldFindAndUpdateDescription() {
        Integer id = 1;
        String newDescription = "Updated service description";

        Service serviceEntity = new Service();
        serviceEntity.setId(id);
        serviceEntity.setDescription("Old description");

        when(repository.findById(id)).thenReturn(Optional.of(serviceEntity));

        service.updateDescription(id, newDescription);

        assertEquals(newDescription, serviceEntity.getDescription());

        verify(repository).beginTransaction();
        verify(repository).update(serviceEntity);
        verify(repository).commitTransaction();
    }


    @Test
    void updateBasePrice_shouldFindAndUpdatePrice() {
        Integer id = 1;
        Double newPrice = 120.5;

        Service serviceEntity = new Service();
        serviceEntity.setId(id);
        serviceEntity.setBasePrice(90.0);

        when(repository.findById(id)).thenReturn(Optional.of(serviceEntity));

        service.updateBasePrice(id, newPrice);

        assertEquals(newPrice, serviceEntity.getBasePrice());

        verify(repository).beginTransaction();
        verify(repository).update(serviceEntity);
        verify(repository).commitTransaction();
    }


    @Test
    void existsByName_shouldReturnRepositoryValue() {
        when(repository.existsByName("Car Wash")).thenReturn(true);

        boolean result = service.existsByName("Car Wash");

        assertTrue(result);

        when(repository.existsByName("Gardening")).thenReturn(false);

        boolean result2 = service.existsByName("Gardening");

        assertFalse(result2);
    }
}
