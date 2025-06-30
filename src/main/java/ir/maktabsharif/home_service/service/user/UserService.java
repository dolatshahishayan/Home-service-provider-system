package ir.maktabsharif.home_service.service.user;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.user.UserSaveUpdateRequest;
import ir.maktabsharif.home_service.model.user.User;

public interface UserService extends BaseService<User> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email,Integer id);
    User login(String email, String password);
    void logout();
}
