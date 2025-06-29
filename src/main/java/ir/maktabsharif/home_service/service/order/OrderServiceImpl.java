package ir.maktabsharif.home_service.service.order;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.order.OrderFindResponse;
import ir.maktabsharif.home_service.dto.order.OrderSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.CouldNotUpdateException;
import ir.maktabsharif.home_service.mapper.order.OrderMapper;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.expert_service.Expert_Service;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.repository.order.OrderRepository;
import ir.maktabsharif.home_service.service.expert_service.Expert_ServiceService;
import ir.maktabsharif.home_service.service.suggestion.SuggestionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl extends BaseServiceImpl<Order, OrderSaveUpdateRequest, OrderRepository, OrderMapper> implements OrderService {
    protected final SuggestionService suggestionService;
    protected final Expert_ServiceService expert_ServiceService;

    public OrderServiceImpl(OrderRepository repository, OrderMapper mapper, SuggestionService suggestionService, Expert_ServiceService expertServiceService) {
        super(repository, mapper);
        this.suggestionService = suggestionService;
        expert_ServiceService = expertServiceService;
    }
    @Override
    public void saveWithDTO(OrderSaveUpdateRequest orderSaveUpdateRequest) {
        Order order = mapper.mapToEntity(orderSaveUpdateRequest);
        order.setOrderStatus(OrderStatus.WAITING_FOR_EXPERT_SUGGESTION);
        order.setCreationDate(LocalDateTime.now());
        save(order);
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
        update(byId);
    }

    private Order setExpertForOrder(Integer suggestionId) {
        Suggestion suggestion = suggestionService.findById(suggestionId);
        Order order = findById(suggestion.getOrder().getId());
        if (order.getExpert() != null) {
            throw new CouldNotUpdateException("You have already chose an expert for this order.");
        }
        order.setExpert(suggestion.getExpert());
        update(order);
        return order;
    }
    @Override
    public List<OrderFindResponse> findAllByExpertId(Integer expertId) {
        List<Expert_Service> expert_services = expert_ServiceService.findByExpertId(expertId);
        List<OrderFindResponse> responses = new ArrayList<>();
        for (Expert_Service expert_service : expert_services) {
            List<Order> byServiceId = findByServiceId(expert_service.getService().getId());
            for (Order order : byServiceId) {
                responses.add(mapper.mapToResponse(order));
            }
        }
        return responses;
    }
    @Override
    public void updateWithDTO(OrderSaveUpdateRequest orderSaveUpdateRequest) {
        update(mapper.mapToEntity(orderSaveUpdateRequest));
    }

    @Override
    public boolean existsBySpecialistAndOrderStatusIn(Expert expert, List<OrderStatus> statuses) {
        return repository.existsBySpecialistAndOrderStatusIn(expert, statuses);
    }

    @Override
    public List<Order> findByServiceId(Integer serviceId) {
        return repository.findByServiceId(serviceId);
    }
}
