package ir.maktabsharif.home_service.base.service;

import ir.maktabsharif.home_service.base.repository.CrudRepository;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class BaseServiceImpl<T,U, R extends CrudRepository<T>> implements BaseService<T,U> {
    private final R repository;

    @Override
    public void save(T t) {
        repository.beginTransaction();
        repository.save(t);
        repository.commitTransaction();
    }

    @Override
    public void update(T t) {
        repository.beginTransaction();
        repository.update(t);
        repository.commitTransaction();
    }

    @Override
    public void delete(T t) {
        repository.beginTransaction();
        repository.delete(t);
        repository.commitTransaction();
    }

    @Override
    public T findById(Integer id) {
        Optional<T> byId = repository.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        }
        throw new NoElementFoundException();
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }
}
