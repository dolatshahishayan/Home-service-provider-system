package ir.maktabsharif.home_service.base.repository;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T> {
    void save(T t);
    void delete(T t);
    void update(T t);
    List<T> findAll();
    Optional<T> findById(Integer id);
    void beginTransaction();
    void commitTransaction();
}
