package ir.maktabsharif.home_service.base.service;

import ir.maktabsharif.home_service.exception.NoElementFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public class BaseServiceImpl<T,ID, R extends JpaRepository<T, ID>, M> implements BaseService<T,ID> {
    public final R repository;
    public final M mapper;
    public BaseServiceImpl(R repository, M mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public T save(T t) {
        return repository.save(t);
    }

    @Override
    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    @Override
    public T findById(ID id) {
        return repository.findById(id).orElseThrow(NoElementFoundException::new);

    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }


    @Override
    public void delete(T t) {
        repository.delete(t);
    }
}
