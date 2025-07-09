package ir.maktabsharif.home_service.service.user;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.user.LoginDTO;
import ir.maktabsharif.home_service.dto.user.UserSearchRequestDTO;
import ir.maktabsharif.home_service.dto.user.UserSearchResponseDTO;
import ir.maktabsharif.home_service.model.user.User;

import java.util.List;

public interface UserService extends BaseService<User, Integer> {
    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Integer id);

    User findByEmailAndPassword(LoginDTO loginDTO);

    List<UserSearchResponseDTO> searchUsers(UserSearchRequestDTO userSearchRequestDTO);
}
