package ir.maktabsharif.home_service.repository.user;

import ir.maktabsharif.home_service.model.user.User;
import org.mapstruct.control.MappingControl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer>, JpaSpecificationExecutor<User> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email,Integer id);
    Optional<User> findByEmailAndPassword(String email, String password);
}
