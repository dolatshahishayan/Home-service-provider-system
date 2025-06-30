package ir.maktabsharif.home_service.service.user;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.user.UserSessionDTO;
import ir.maktabsharif.home_service.exception.NoUserFoundWithGivenCredentialsException;
import ir.maktabsharif.home_service.exception.NoUserLoggedInException;
import ir.maktabsharif.home_service.mapper.user.UserMapper;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.repository.user.UserRepository;
import ir.maktabsharif.home_service.util.Session;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, UserRepository, UserMapper> implements UserService {
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
    public User login(String email, String password) {
        User byEmailAndPassword = repository.findByEmailAndPassword(email, password);
        if (byEmailAndPassword == null) {
            throw new NoUserFoundWithGivenCredentialsException();
        }
        Session.setCurrentUser(new UserSessionDTO(byEmailAndPassword.getId(), byEmailAndPassword.getEmail()));
        return byEmailAndPassword;
    }
    @Override
    public void logout(){
        if (Session.getCurrentUser() == null) {
            throw new NoUserLoggedInException("No user logged in.");
        }
        Session.setCurrentUser(null);
    }

}
