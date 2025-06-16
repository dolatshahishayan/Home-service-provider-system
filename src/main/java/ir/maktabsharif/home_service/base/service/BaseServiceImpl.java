package ir.maktabsharif.home_service.base.service;

import ir.maktabsharif.home_service.base.repository.CrudRepository;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class BaseServiceImpl<T, U, R extends CrudRepository<T>, W> implements BaseService<T, U> {
    public final R repository;
    public final W mapper;

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
    public void delete(Integer id) {
        repository.beginTransaction();
        repository.delete(id);
        repository.commitTransaction();
    }

    @Override
    public T findById(Integer id) {
        Optional<T> byId = repository.findById(id);
        if (byId.isEmpty()) {
            throw new NoElementFoundException();
        }
        return byId.get();
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }
}
