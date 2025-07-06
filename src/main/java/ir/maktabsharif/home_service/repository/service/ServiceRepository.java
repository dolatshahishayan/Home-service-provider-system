package ir.maktabsharif.home_service.repository.service;

import ir.maktabsharif.home_service.model.service.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends JpaRepository<Service,Integer>, JpaSpecificationExecutor<Service> {
    boolean existsByName(String name);

    List<Service> findByParentServiceIsNull();

    List<Service> findByParentService(Service parent);
}
