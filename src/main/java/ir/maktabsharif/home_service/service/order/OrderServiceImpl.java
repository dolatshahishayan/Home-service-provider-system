package ir.maktabsharif.home_service.service.order;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.order.OrderFindResponse;
import ir.maktabsharif.home_service.dto.order.OrderSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.user.UserSessionDTO;
import ir.maktabsharif.home_service.exception.CouldNotUpdateException;
import ir.maktabsharif.home_service.exception.InvalidRequestException;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import ir.maktabsharif.home_service.mapper.order.OrderMapper;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.expert_service.ExpertService;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.repository.order.OrderRepository;
import ir.maktabsharif.home_service.service.customer.CustomerService;
import ir.maktabsharif.home_service.service.expert_service.ExpertServiceService;
import ir.maktabsharif.home_service.service.service.ServiceService;
import ir.maktabsharif.home_service.service.suggestion.SuggestionService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl extends BaseServiceImpl<Order, Integer, OrderRepository, OrderMapper> implements OrderService {
    protected final SuggestionService suggestionService;
    protected final ExpertServiceService expert_ServiceService;
    protected final CustomerService customerService;
    protected final ir.maktabsharif.home_service.service.expert.ExpertService expertService;
    protected final ServiceService serviceService;

    public OrderServiceImpl(OrderRepository repository, OrderMapper mapper, SuggestionService suggestionService, ExpertServiceService expertServiceService, CustomerService customerService, ir.maktabsharif.home_service.service.expert.ExpertService expertService, ServiceService serviceService) {
        super(repository, mapper);
        this.suggestionService = suggestionService;
        expert_ServiceService = expertServiceService;
        this.customerService = customerService;
        this.expertService = expertService;
        this.serviceService = serviceService;
    }

    @Override
    public Order saveWithDTO(OrderSaveUpdateRequest orderSaveUpdateRequest) {
        Order order = mapper.mapToEntity(orderSaveUpdateRequest);
        order.setCustomer(customerService.findById(orderSaveUpdateRequest.getCustomerId()));
        order.setExpert(expertService.findById(orderSaveUpdateRequest.getExpertId()));
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
        Order order = setExpertForOrder(suggestionId);
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

    private Order setExpertForOrder(Integer suggestionId) {
        Suggestion suggestion = suggestionService.findById(suggestionId);
        Order order = findById(suggestion.getOrder().getId());
        if (order.getExpert() != null) {
            throw new CouldNotUpdateException("You have already chose an expert for this order.");
        }
        order.setExpert(suggestion.getExpert());
        return save(order);
    }

    @Override
    public List<OrderFindResponse> findAllByExpertId(Integer expertId) {
        List<ExpertService> expert_services = expert_ServiceService.findByExpertId(expertId);
        List<OrderFindResponse> responses = new ArrayList<>();
        for (ExpertService expert_service : expert_services) {
            List<Order> byServiceId = findByServiceId(expert_service.getService().getId());
            for (Order order : byServiceId) {
                responses.add(mapper.mapToResponse(order));
            }
        }
        return responses;
    }

    @Override
    public Order updateWithDTO(OrderSaveUpdateRequest orderSaveUpdateRequest) {
        Order order = mapper.mapToEntity(orderSaveUpdateRequest);
        order.setCustomer(customerService.findById(orderSaveUpdateRequest.getCustomerId()));
        order.setExpert(expertService.findById(orderSaveUpdateRequest.getExpertId()));
        order.setService(serviceService.findById(orderSaveUpdateRequest.getServiceId()));
        return save(order);
    }

    private Order updateStatus(Integer orderId, OrderStatus newStatus, UserSessionDTO currentUser) {
        Order order = findById(orderId);
        if (!order.getCustomer().getEmail().equals(currentUser.getEmail())) {
            throw new CouldNotUpdateException("You can't update the status of this order.");
        }
        order.setOrderStatus(newStatus);
        return save(order);
    }

    @Override
    public Order updateStatusToStarted(Integer orderId, UserSessionDTO currentUser) {
        return updateStatus(orderId, OrderStatus.STARTED, currentUser);
    }

    @Override
    public Order updateStatusToDone(Integer orderId, UserSessionDTO currentUser) {
        return updateStatus(orderId, OrderStatus.DONE, currentUser);
    }

    @Override
    public boolean existsBySpecialistAndOrderStatusIn(Expert expert, List<OrderStatus> statuses) {
        return repository.existsByExpertAndOrderStatusIn(expert, statuses);
    }

    @Override
    public List<Order> findByServiceId(Integer serviceId) {
        return repository.findByServiceId(serviceId).orElseThrow(NoElementFoundException::new);

    }
}
