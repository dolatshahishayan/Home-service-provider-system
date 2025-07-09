package ir.maktabsharif.home_service.service.user;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.user.LoginDTO;
import ir.maktabsharif.home_service.dto.user.UserSearchRequestDTO;
import ir.maktabsharif.home_service.dto.user.UserSearchResponseDTO;
import ir.maktabsharif.home_service.exception.NoUserFoundWithGivenCredentialsException;
import ir.maktabsharif.home_service.mapper.user.UserMapper;
import ir.maktabsharif.home_service.model.enums.Role;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.repository.user.UserRepository;
import ir.maktabsharif.home_service.service.customer.CustomerService;
import ir.maktabsharif.home_service.service.expert_service.ExpertServiceService;
import ir.maktabsharif.home_service.util.specification.CustomerSpecification;
import ir.maktabsharif.home_service.util.specification.ExpertSpecification;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl extends BaseServiceImpl<User, Integer, UserRepository, UserMapper> implements UserService {
    protected final ExpertServiceService expertServiceService;
    protected final ir.maktabsharif.home_service.service.expert.ExpertService expertService;
    protected final CustomerService customerService;

    public UserServiceImpl(UserRepository repository, UserMapper userMapper, @Lazy ExpertServiceService expertServiceService, @Lazy ir.maktabsharif.home_service.service.expert.ExpertService expertService,@Lazy CustomerService customerService) {
        super(repository, userMapper);
        this.expertServiceService = expertServiceService;
        this.expertService = expertService;
        this.customerService = customerService;
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

    @Override
    public List<UserSearchResponseDTO> searchUsers(UserSearchRequestDTO userSearchRequestDTO) {
        List<UserSearchResponseDTO> results = new ArrayList<>();

        boolean hasExpertFilters = (userSearchRequestDTO.getServiceIds() != null && !userSearchRequestDTO.getServiceIds().isEmpty()) || userSearchRequestDTO.getMinScore() != null || userSearchRequestDTO.getMaxScore() != null;

        if (userSearchRequestDTO.getRole() == Role.EXPERT || (userSearchRequestDTO.getRole() == null && hasExpertFilters)) {
            List<Integer> expertIds = expertServiceService.findExpertIdsByServiceIds(userSearchRequestDTO.getServiceIds());

            Specification<Expert> expertSpec = (_, _, cb) -> cb.conjunction();

            if (userSearchRequestDTO.getName() != null && !userSearchRequestDTO.getName().isBlank())
                expertSpec = expertSpec.and(ExpertSpecification.nameContains(userSearchRequestDTO.getName()));

            if (userSearchRequestDTO.getMinScore() != null || userSearchRequestDTO.getMaxScore() != null)
                expertSpec = expertSpec.and(ExpertSpecification.scoreBetween(userSearchRequestDTO.getMinScore(), userSearchRequestDTO.getMaxScore()));

            if (!expertIds.isEmpty())
                expertSpec = expertSpec.and((root, _, _) -> root.get("id").in(expertIds));

            List<Expert> experts = expertService.findAll(expertSpec);
            for (Expert expert : experts) {
                results.add(mapper.mapExpertToSearchResponse(expert));
            }
        }

        if (userSearchRequestDTO.getRole() == Role.CUSTOMER || (userSearchRequestDTO.getRole() == null && !hasExpertFilters)) {
            Specification<Customer> customerSpec = CustomerSpecification.nameContains(userSearchRequestDTO.getName());
            List<Customer> customers = customerService.findAll(customerSpec);
            for (Customer customer : customers) {
                results.add(mapper.mapCustomerToSearchResponse(customer));
            }
        }
        return results;
    }

}
