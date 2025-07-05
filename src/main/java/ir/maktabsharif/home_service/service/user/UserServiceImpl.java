package ir.maktabsharif.home_service.service.user;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.user.LoginDTO;
import ir.maktabsharif.home_service.exception.NoUserFoundWithGivenCredentialsException;
import ir.maktabsharif.home_service.mapper.user.UserMapper;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl extends BaseServiceImpl<User, Integer, UserRepository, UserMapper> implements UserService {

    public UserServiceImpl(UserRepository repository, UserMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, Integer id) {
        return repository.existsByEmailAndIdNot(email, id);
    }

    @Override
    public User findByEmailAndPassword(LoginDTO loginDTO) {
        return repository.findByEmailAndPassword(loginDTO.getEmail().toLowerCase(), loginDTO.getPassword()).orElseThrow(NoUserFoundWithGivenCredentialsException::new);

    }

}
