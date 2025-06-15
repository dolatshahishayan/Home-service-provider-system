package ir.maktabsharif.home_service.base.service;

import java.util.List;

public interface BaseService<T,U> {
    void save(T t);
    void update(T t);
    void delete(T t);
    T findById(Integer id);
    List<T> findAll();
    void saveWithDTO(U u);
    void updateWithDTO(U u);
}
