package ir.maktabsharif.home_service.service.suggestion;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.suggestion.SuggestionFindResponse;
import ir.maktabsharif.home_service.dto.suggestion.SuggestionSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.InvalidRequestException;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import ir.maktabsharif.home_service.mapper.suggestion.SuggestionMapper;
import ir.maktabsharif.home_service.model.enums.ExpertStatus;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.model.user.UserDetailsImpl;
import ir.maktabsharif.home_service.repository.suggestion.SuggestionRepository;
import ir.maktabsharif.home_service.service.expert.ExpertService;
import ir.maktabsharif.home_service.service.expert_service.ExpertServiceService;
import ir.maktabsharif.home_service.service.order.OrderService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class SuggestionServiceImpl extends BaseServiceImpl<Suggestion, Integer, SuggestionRepository, SuggestionMapper> implements SuggestionService {
    protected final ExpertService expertService;
    protected final OrderService orderService;
    protected final ExpertServiceService expertServiceService;

    public SuggestionServiceImpl(SuggestionRepository repository, SuggestionMapper suggestionMapper, @Lazy ExpertService expertService, @Lazy OrderService orderService,@Lazy ExpertServiceService expertServiceService) {
        super(repository, suggestionMapper);
        this.expertService = expertService;
        this.orderService = orderService;
        this.expertServiceService = expertServiceService;
    }

    @Override
    public Suggestion registerSuggestionForOrder(SuggestionSaveUpdateRequest suggestionSaveUpdateRequest) {
        Suggestion suggestion = mapper.mapToEntity(suggestionSaveUpdateRequest);
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Expert byId = expertService.findById(principal.user().getId());
        checkPriceAndOrderStatus(suggestionSaveUpdateRequest, byId, suggestion, principal);
        suggestion.setCreationDate(LocalDateTime.now());
        suggestion.setExpert(byId);
        suggestion.setAccepted(false);
        Suggestion save = save(suggestion);
        setOrderStatusToWaitingForExpert(suggestion);
        return save;
    }

    private void setOrderStatusToWaitingForExpert(Suggestion suggestion) {
        if (suggestion.getOrder().getOrderStatus().equals(OrderStatus.WAITING_FOR_EXPERT_SUGGESTION)) {
            suggestion.getOrder().setOrderStatus(OrderStatus.WAITING_TO_CHOOSE_EXPERT);
            orderService.save(suggestion.getOrder());
        }
    }

    private void checkPriceAndOrderStatus(SuggestionSaveUpdateRequest suggestionSaveUpdateRequest, Expert byId, Suggestion suggestion, UserDetailsImpl principal) {
        checkExpertStatusAndExpertWithServiceExists(suggestionSaveUpdateRequest, byId, suggestion, principal);
        if (!(suggestion.getOrder().getOrderStatus().equals(OrderStatus.WAITING_FOR_EXPERT_SUGGESTION) || suggestion.getOrder().getOrderStatus().equals(OrderStatus.WAITING_TO_CHOOSE_EXPERT))) {
            throw new InvalidRequestException("Order is not waiting for any suggestions.");
        }
        if (suggestion.getPrice() < suggestion.getOrder().getService().getBasePrice()) {
            throw new InvalidRequestException("Price must be greater than the base price.");
        }
    }

    private void checkExpertStatusAndExpertWithServiceExists(SuggestionSaveUpdateRequest suggestionSaveUpdateRequest, Expert byId, Suggestion suggestion, UserDetailsImpl principal) {
        if (byId.getExpertStatus()!= ExpertStatus.VERIFIED){
            throw new InvalidRequestException("Expert status must be VERIFIED");
        }
        suggestion.setOrder(orderService.findById(suggestionSaveUpdateRequest.getOrderId()));
        if (!expertServiceService.existsByExpertIdAndServiceId(principal.user().getId(), suggestion.getOrder().getService().getId())) {
            throw new InvalidRequestException("Expert Id and Service Id are not registered");
        }
    }

    @Override
    public Suggestion updateWithDTO(SuggestionSaveUpdateRequest suggestionSaveUpdateRequest) {
        Suggestion suggestion = findById(suggestionSaveUpdateRequest.getId());
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!expertServiceService.existsByExpertIdAndServiceId(principal.user().getId(), suggestion.getOrder().getService().getId())) {
            throw new InvalidRequestException("Expert Id and Service Id are not registered");
        }
        mapper.updateEntityWithDTO(suggestionSaveUpdateRequest, suggestion);
        suggestion.setExpert(expertService.findById(principal.user().getId()));
        return save(suggestion);
    }

    @Override
    public Page<Suggestion> findAllAndSortByPriceAsc(Integer orderId, Pageable pageable) {
        Order byId = orderService.findById(orderId);
        Page<Suggestion> allByOrderAndSortByPriceAsc = repository.findAllByOrderAndSortByPriceAsc(byId,pageable);
        if (allByOrderAndSortByPriceAsc.getContent().isEmpty()) {
            throw new NoElementFoundException();
        }
        return allByOrderAndSortByPriceAsc;
    }

    @Override
    public Page<Suggestion> findAllByAndSortByExpertScoreDesc(Integer orderId,Pageable pageable) {
        Order byId = orderService.findById(orderId);
        Page<Suggestion> allByOrderAndSortByExpertScoreDesc = repository.findAllByOrderAndSortByExpertScoreDesc(byId,pageable);
        if (allByOrderAndSortByExpertScoreDesc.getContent().isEmpty()) {
            throw new NoElementFoundException();
        }
        return allByOrderAndSortByExpertScoreDesc;
    }

    @Override
    public boolean existsByOrderIdAndExpertIdAndAcceptedTrue(Integer orderId, Integer expertId) {
        return repository.existsByOrderIdAndExpertIdAndAcceptedTrue(orderId, expertId);
    }

    @Override
    public Page<SuggestionFindResponse> findAllByExpertId(Integer expertId,Pageable pageable) {
        Page<Suggestion> allByExpertId = repository.findAllByExpertId(expertId,pageable);
        if (allByExpertId.getContent().isEmpty()) {
            throw new NoElementFoundException();
        }
        return allByExpertId.map(mapper::mapToResponse);
    }

    @Override
    public void confirmSuggestionAcceptance(Integer suggestionId) {
        Suggestion suggestion = findById(suggestionId);
        suggestion.setAccepted(true);
        save(suggestion);
    }

    @Override
    public Suggestion findByOrderId(Integer orderId) {
        return repository.findByOrderId(orderId).orElseThrow(NoElementFoundException::new);
    }
}
