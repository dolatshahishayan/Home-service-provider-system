package ir.maktabsharif.home_service.service.user;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.user.UserSaveUpdateRequest;
import ir.maktabsharif.home_service.mapper.user.UserMapper;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.repository.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, UserSaveUpdateRequest, UserRepository, UserMapper> implements UserService {
    public UserServiceImpl(UserRepository repository, UserMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
}
