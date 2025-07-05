package ir.maktabsharif.home_service.base.service;

import java.util.List;

public interface BaseService<T,ID> {
    T save(T t);
    void delete(ID id);
    T findById(ID id);
    List<T> findAll();
}
