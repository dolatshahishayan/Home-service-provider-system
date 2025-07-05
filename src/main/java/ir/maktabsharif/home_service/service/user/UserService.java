package ir.maktabsharif.home_service.service.user;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.user.LoginDTO;
import ir.maktabsharif.home_service.model.user.User;

public interface UserService extends BaseService<User, Integer> {
    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Integer id);

    User findByEmailAndPassword(LoginDTO loginDTO);
}
