package ir.maktabsharif.home_service.base.service;

import ir.maktabsharif.home_service.base.repository.CrudRepository;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class BaseServiceImplTest {

    static class TestEntity {
        Integer id;
        String name;

        TestEntity(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Mock
    CrudRepository<TestEntity> repository;

    @Mock
    Object mapper;

    BaseServiceImpl<TestEntity, Object, CrudRepository<TestEntity>, Object> service;

    @BeforeEach
    void setUp() {
        service = new BaseServiceImpl<>(repository, mapper);
    }

    @Test
    void testSave() {
        TestEntity entity = new TestEntity(1, "Ali");

        doNothing().when(repository).beginTransaction();
        doNothing().when(repository).commitTransaction();

        service.save(entity);

        verify(repository).beginTransaction();
        verify(repository).save(entity);
        verify(repository).commitTransaction();
    }

    @Test
    void testFindById_found() {
        TestEntity entity = new TestEntity(2, "Sara");
        when(repository.findById(2)).thenReturn(Optional.of(entity));

        TestEntity result = service.findById(2);

        assertEquals(entity, result);
    }

    @Test
    void testFindById_notFound() {
        when(repository.findById(10)).thenReturn(Optional.empty());

        assertThrows(NoElementFoundException.class, () -> service.findById(10));
    }

    @Test
    void testDelete() {
        doNothing().when(repository).beginTransaction();
        doNothing().when(repository).delete(1);
        doNothing().when(repository).commitTransaction();

        service.delete(1);

        verify(repository).beginTransaction();
        verify(repository).delete(1);
        verify(repository).commitTransaction();
    }

    @Test
    void testUpdate() {
        TestEntity entity = new TestEntity(3, "Zahra");

        doNothing().when(repository).beginTransaction();
        doNothing().when(repository).update(entity);
        doNothing().when(repository).commitTransaction();

        service.update(entity);

        verify(repository).beginTransaction();
        verify(repository).update(entity);
        verify(repository).commitTransaction();
    }

    @Test
    void testFindAll() {
        List<TestEntity> list = List.of(new TestEntity(1, "A"), new TestEntity(2, "B"));
        when(repository.findAll()).thenReturn(list);

        List<TestEntity> result = service.findAll();

        assertEquals(2, result.size());
    }
}
