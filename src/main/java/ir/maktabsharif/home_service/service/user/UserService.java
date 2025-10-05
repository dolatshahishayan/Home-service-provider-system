package ir.maktabsharif.home_service.service.user;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.user.LoginDTO;
import ir.maktabsharif.home_service.dto.user.UserSearchRequestDTO;
import ir.maktabsharif.home_service.dto.user.UserSearchResponseDTO;
import ir.maktabsharif.home_service.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService extends BaseService<User, Integer> {
    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Integer id);

    Page<UserSearchResponseDTO> searchUsers(UserSearchRequestDTO userSearchRequestDTO, Pageable pageable);

    User findByEmail(String email);

    void deleteAll();

    UserDetails loadUserByUsername(String username);
}
