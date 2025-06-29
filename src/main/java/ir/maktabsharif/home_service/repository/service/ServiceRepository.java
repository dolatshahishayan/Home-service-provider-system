package ir.maktabsharif.home_service.repository.service;

import ir.maktabsharif.home_service.base.repository.CrudRepository;
import ir.maktabsharif.home_service.model.service.Service;

public interface ServiceRepository extends CrudRepository<Service> {
    boolean existsByName(String name);
}
