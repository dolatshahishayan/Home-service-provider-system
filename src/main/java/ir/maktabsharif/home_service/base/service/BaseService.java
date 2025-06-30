package ir.maktabsharif.home_service.base.service;

import java.util.List;

public interface BaseService<T> {
    void save(T t);
    void update(T t);
    void delete(Integer id);
    T findById(Integer id);
    List<T> findAll();
}
