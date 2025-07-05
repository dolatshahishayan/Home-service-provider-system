package ir.maktabsharif.home_service.repository.admin;

import ir.maktabsharif.home_service.model.user.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin,Integer>, JpaSpecificationExecutor<Admin> {
    Optional<Admin> findByEmail(String email);
}
