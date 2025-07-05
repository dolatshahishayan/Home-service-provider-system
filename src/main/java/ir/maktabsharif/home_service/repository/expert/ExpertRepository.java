package ir.maktabsharif.home_service.repository.expert;

import ir.maktabsharif.home_service.model.user.Expert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ExpertRepository extends JpaRepository<Expert,Integer>, JpaSpecificationExecutor<Expert> {
    Optional<Expert> findByEmail(String email);
}
