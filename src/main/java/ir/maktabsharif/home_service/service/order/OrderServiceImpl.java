package ir.maktabsharif.home_service.service.order;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.order.OrderSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.order.OrderSummaryDTO;
import ir.maktabsharif.home_service.exception.CouldNotUpdateException;
import ir.maktabsharif.home_service.exception.InvalidRequestException;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import ir.maktabsharif.home_service.mapper.order.OrderMapper;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.model.user.UserDetailsImpl;
import ir.maktabsharif.home_service.repository.order.OrderRepository;
import ir.maktabsharif.home_service.service.customer.CustomerService;
import ir.maktabsharif.home_service.service.expert_service.ExpertServiceService;
import ir.maktabsharif.home_service.service.service.ServiceService;
import ir.maktabsharif.home_service.service.suggestion.SuggestionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl extends BaseServiceImpl<Order, Integer, OrderRepository, OrderMapper> implements OrderService {
    protected final SuggestionService suggestionService;
    protected final ExpertServiceService expert_ServiceService;
    protected final CustomerService customerService;
    protected final ir.maktabsharif.home_service.service.expert.ExpertService expertService;
    protected final ServiceService serviceService;

    public OrderServiceImpl(OrderRepository repository, OrderMapper orderMapper, SuggestionService suggestionService, ExpertServiceService expertServiceService, CustomerService customerService, ir.maktabsharif.home_service.service.expert.ExpertService expertService, ServiceService serviceService) {
        super(repository, orderMapper);
        this.suggestionService = suggestionService;
        expert_ServiceService = expertServiceService;
        this.customerService = customerService;
        this.expertService = expertService;
        this.serviceService = serviceService;
    }

    @Override
    public Order saveWithDTO(OrderSaveUpdateRequest orderSaveUpdateRequest) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Order order = mapper.mapToEntity(orderSaveUpdateRequest);
        if (principal.user().getId() == null) {
            throw new InvalidRequestException("Customer Id is required");
        }
        order.setCustomer(customerService.findById(principal.user().getId()));
        if (orderSaveUpdateRequest.getExpertId() != null) {
            order.setExpert(expertService.findById(orderSaveUpdateRequest.getExpertId()));
        }
        order.setService(serviceService.findById(orderSaveUpdateRequest.getServiceId()));
        if (order.getProposedPrice() < order.getService().getBasePrice()) {
            throw new InvalidRequestException("Proposed price must be greater than the service price");
        }
        order.setOrderStatus(OrderStatus.WAITING_FOR_EXPERT_SUGGESTION);
        order.setCreationDate(LocalDateTime.now());
        return save(order);
    }

    @Override
    public void chooseExpert(Integer suggestionId) {
        Order order = setExpertAndFinalPriceForOrder(suggestionId);
        if (order.getExpert() == null) {
            throw new CouldNotUpdateException("Couldn't register expert for this order.");
        }
        updateOrderStatusToWaitingForExpertToVisit(order.getId());
        suggestionService.confirmSuggestionAcceptance(suggestionId);
    }

    private void updateOrderStatusToWaitingForExpertToVisit(Integer id) {
        Order byId = findById(id);
        byId.setOrderStatus(OrderStatus.WAITING_FOR_EXPERT_TO_VISIT);
        save(byId);
    }

    private Order setExpertAndFinalPriceForOrder(Integer suggestionId) {
        Suggestion suggestion = suggestionService.findById(suggestionId);
        Order order = findById(suggestion.getOrder().getId());
        if (order.getExpert() != null) {
            throw new CouldNotUpdateException("You have already chose an expert for this order.");
        }
        order.setExpert(suggestion.getExpert());
        order.setFinalPrice(suggestion.getPrice());
        return save(order);
    }

    @Override
    public Page<OrderSummaryDTO> findAllByExpertId(Pageable pageable) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Page<Order> byExpertId = repository.findByExpertId(principal.user().getId(), pageable);
        return byExpertId.map(mapper::mapToSummary);
    }

    @Override
    public boolean existsByOrderIdAndExpertIdAndAcceptedTrue(Integer orderId) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return suggestionService.existsByOrderIdAndExpertIdAndAcceptedTrue(orderId, principal.user().getId());
    }

    @Override
    public Order updateWithDTO(OrderSaveUpdateRequest orderSaveUpdateRequest) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Order order = findById(orderSaveUpdateRequest.getId());
        mapper.updateEntityWithDTO(orderSaveUpdateRequest, order);
        if (principal.user().getId() == null) {
            throw new InvalidRequestException("Customer Id is required");
        }
        order.setCustomer(customerService.findById(principal.user().getId()));
        if (orderSaveUpdateRequest.getExpertId() != null) {
            order.setExpert(expertService.findById(orderSaveUpdateRequest.getExpertId()));
        }
        return save(order);
    }

    private Order updateStatus(Integer orderId, OrderStatus newStatus, UserDetailsImpl currentUser) {
        Order order = findById(orderId);
        if (!order.getCustomer().getEmail().equals(currentUser.user().getEmail())) {
            throw new CouldNotUpdateException("You can't update the status of this order.");
        }
        order.setOrderStatus(newStatus);
        return save(order);
    }

    @Override
    public Order updateStatusToStarted(Integer orderId) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Order order = findById(orderId);
        if (!order.getStartDate().isBefore(LocalDateTime.now())) {
            throw new CouldNotUpdateException("You can't update order status to started before the start date.");
        }
        return updateStatus(orderId, OrderStatus.STARTED, principal);
    }

    @Override
    public Order updateStatusToDone(Integer orderId) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return updateStatus(orderId, OrderStatus.DONE, principal);
    }

    @Override
    public long reduce1ScoreFromExpertPerHour(Order order) {
        if (!order.getStartDate().isBefore(LocalDateTime.now())) {
            throw new CouldNotUpdateException("It's not the order's date.");
        }
        return ChronoUnit.HOURS.between(order.getStartDate(), LocalDateTime.now());
    }

    @Override
    public Page<Order> findByCustomerId(Pageable pageable) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return repository.findByCustomerId(principal.user().getId(),pageable);
    }

    @Override
    public boolean existsBySpecialistAndOrderStatusIn(Integer expertId, List<OrderStatus> statuses) {
        Expert expert = expertService.findById(expertId);
        return repository.existsByExpertAndOrderStatusIn(expert, statuses);
    }

    @Override
    public Page<Order> findByServiceId(Integer serviceId, Pageable pageable) {
        Page<Order> byServiceId = repository.findByServiceId(serviceId, pageable);
        if (byServiceId.getContent().isEmpty()) {
            throw new NoElementFoundException();
        }
        return byServiceId;
    }
}
