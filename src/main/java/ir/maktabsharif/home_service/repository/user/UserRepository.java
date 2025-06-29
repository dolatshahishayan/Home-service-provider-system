package ir.maktabsharif.home_service.repository.user;

import ir.maktabsharif.home_service.base.repository.CrudRepository;
import ir.maktabsharif.home_service.model.user.User;

public interface UserRepository extends CrudRepository<User> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email,Integer id);
    User findByEmailAndPassword(String email,String password);
}
