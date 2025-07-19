package ir.maktabsharif.home_service.repository.service;

import ir.maktabsharif.home_service.model.service.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceRepository extends JpaRepository<Service,Integer>, JpaSpecificationExecutor<Service> {
    boolean existsByName(String name);

    Page<Service> findByParentServiceIsNull(Pageable pageable);

    Page<Service> findByParentService(Service parent,Pageable pageable);
}
