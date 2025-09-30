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
import ir.maktabsharif.home_service.model.user.UserDetailsImpl;
import ir.maktabsharif.home_service.repository.user.UserRepository;
import ir.maktabsharif.home_service.service.customer.CustomerService;
import ir.maktabsharif.home_service.service.expert_service.ExpertServiceService;
import ir.maktabsharif.home_service.util.specification.CustomerSpecification;
import ir.maktabsharif.home_service.util.specification.ExpertSpecification;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl extends BaseServiceImpl<User, Integer, UserRepository, UserMapper> implements UserService, UserDetailsService {
    protected final ExpertServiceService expertServiceService;
    protected final ir.maktabsharif.home_service.service.expert.ExpertService expertService;
    protected final CustomerService customerService;

    public UserServiceImpl(UserRepository repository, UserMapper userMapper, @Lazy ExpertServiceService expertServiceService, @Lazy ir.maktabsharif.home_service.service.expert.ExpertService expertService, @Lazy CustomerService customerService) {
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
    public Page<UserSearchResponseDTO> searchUsers(UserSearchRequestDTO userSearchRequestDTO, Pageable pageable) {
        List<UserSearchResponseDTO> results = new ArrayList<>();
        boolean hasExpertFilters = (userSearchRequestDTO.getServiceIds() != null && !userSearchRequestDTO.getServiceIds().isEmpty()) || userSearchRequestDTO.getMinScore() != null || userSearchRequestDTO.getMaxScore() != null;
        if (userSearchRequestDTO.getRole() == Role.ROLE_EXPERT || (userSearchRequestDTO.getRole() == null && hasExpertFilters)) {
            checkAndAddExpertToResults(userSearchRequestDTO, pageable, results);
        }
        if (userSearchRequestDTO.getRole() == Role.ROLE_CUSTOMER || (userSearchRequestDTO.getRole() == null && !hasExpertFilters)) {
            checkAndAddCustomerToResults(userSearchRequestDTO, pageable, results);
        }
        List<UserSearchResponseDTO> pagedList = getUserSearchResponseDTOS(pageable, results);
        return new PageImpl<>(pagedList, pageable, results.size());
    }

    private static List<UserSearchResponseDTO> getUserSearchResponseDTOS(Pageable pageable, List<UserSearchResponseDTO> results) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), results.size());
        return results.subList(start, end);
    }

    private void checkAndAddCustomerToResults(UserSearchRequestDTO userSearchRequestDTO, Pageable pageable, List<UserSearchResponseDTO> results) {
        Specification<Customer> customerSpec = CustomerSpecification.nameContains(userSearchRequestDTO.getName());
        Page<Customer> customers = customerService.findAll(customerSpec, pageable);
        customers.getContent().forEach(customer -> results.add(mapper.mapCustomerToSearchResponse(customer)));
    }

    private void checkAndAddExpertToResults(UserSearchRequestDTO userSearchRequestDTO, Pageable pageable, List<UserSearchResponseDTO> results) {
        List<Integer> expertIds = expertServiceService.findExpertIdsByServiceIds(userSearchRequestDTO.getServiceIds());
        Specification<Expert> expertSpec = (ca, cq, cb) -> cb.conjunction();
        if (userSearchRequestDTO.getName() != null && !userSearchRequestDTO.getName().isBlank())
            expertSpec = expertSpec.and(ExpertSpecification.nameContains(userSearchRequestDTO.getName()));
        if (userSearchRequestDTO.getMinScore() != null || userSearchRequestDTO.getMaxScore() != null)
            expertSpec = expertSpec.and(ExpertSpecification.scoreBetween(userSearchRequestDTO.getMinScore(), userSearchRequestDTO.getMaxScore()));
        if (!expertIds.isEmpty())
            expertSpec = expertSpec.and((root, cq, cb) -> root.get("id").in(expertIds));
        Page<Expert> experts = expertService.findAll(expertSpec, pageable);
        experts.getContent().forEach(expert -> results.add(mapper.mapExpertToSearchResponse(expert)));
    }

    @Override
    public User findByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(NoUserFoundWithGivenCredentialsException::new);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByEmail(username).orElseThrow(NoUserFoundWithGivenCredentialsException::new);
        return new UserDetailsImpl(user);
    }
}
