package ir.maktabsharif.home_service.controller.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.dto.order.OrderFindResponse;
import ir.maktabsharif.home_service.dto.order.OrderSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.order.OrderSearchRequest;
import ir.maktabsharif.home_service.dto.order.OrderSummaryDTO;
import ir.maktabsharif.home_service.mapper.order.OrderMapper;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.user.UserDetailsImpl;
import ir.maktabsharif.home_service.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders controller", description = "Controller class for orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @PostMapping("/save")
    @Operation(summary = "Save order", description = "Method for saving an order")
    public ResponseEntity<OrderFindResponse> save(@RequestBody @Validated(ValidationGroup.Save.class) OrderSaveUpdateRequest order) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Order saved = orderService.saveWithDTO(order,principal.user().getId());
        return ResponseEntity.ok(orderMapper.mapToResponse(saved));
    }

    @PutMapping("/update")
    @Operation(summary = "Update order", description = "Method for updating an order")
    public ResponseEntity<OrderFindResponse> update(@RequestBody @Validated(ValidationGroup.Update.class) OrderSaveUpdateRequest order) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Order updated = orderService.updateWithDTO(order,principal.user().getId());
        return ResponseEntity.ok(orderMapper.mapToResponse(updated));
    }

    @GetMapping("/exists-by-expert-and-order-status-in")
    @Operation(summary = "Exists by expert and order status in", description = "Checks if an expert has an order with given statuses")
    public ResponseEntity<Boolean> existsByExpertAndOrderStatusIn(@RequestParam Integer expertId, @RequestParam List<OrderStatus> orderStatuses) {
        return ResponseEntity.ok(orderService.existsBySpecialistAndOrderStatusIn(expertId, orderStatuses));
    }

    @GetMapping("/find-by-service-id")
    @Operation(summary = "Find by service id", description = "Finds orders by service id")
    public ResponseEntity<Page<OrderFindResponse>> findByServiceId(@RequestParam Integer serviceId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<Order> orders = orderService.findByServiceId(serviceId, PageRequest.of(page, size));
        return ResponseEntity.ok(orders.map(orderMapper::mapToResponse));
    }

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @PutMapping("/choose-expert")
    @Operation(summary = "Choose expert", description = "Method for choosing an expert for an order")
    public ResponseEntity<String> chooseExpert(@RequestParam Integer suggestionId) {
        orderService.chooseExpert(suggestionId);
        return ResponseEntity.ok("Expert chosen");
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/search-orders")
    @Operation(summary = "Search orders",description = "Search order by filters")
    public ResponseEntity<Page<OrderSummaryDTO>> searchOrders(@RequestBody OrderSearchRequest orderSearchRequest,@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.searchOrders(orderSearchRequest,PageRequest.of(page, size)));
    }

    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @GetMapping("/find-all-by-expert")
    @Operation(summary = "Find all by expert", description = "Find all orders for an expert")
    public ResponseEntity<Page<OrderSummaryDTO>> findAllByExpert(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(orderService.findAllByExpertId(PageRequest.of(page, size),principal.user().getId()));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT','ROLE_ADMIN')")
    @GetMapping("/find-order-with-details")
    @Operation(summary = "Find order with details", description = "Find order with details by order id")
    public ResponseEntity<OrderFindResponse> findOrderWithDetails(@RequestParam Integer orderId) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isAccepted = orderService.existsByOrderIdAndExpertIdAndAcceptedTrue(orderId,principal.user().getId());
        if (!isAccepted) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        Order order = orderService.findById(orderId);
        return ResponseEntity.ok(orderMapper.mapToResponse(order));
    }

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @GetMapping("/find-all-by-customer")
    @Operation(summary = "Find all by customer", description = "Find all orders for a customer")
    public ResponseEntity<Page<OrderFindResponse>> findAllByCustomer(@RequestParam(required = false) OrderStatus orderStatus,@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Page<Order> byCustomerId = orderService.findByCustomerId(orderStatus,PageRequest.of(page, size),principal.user().getId());
        return ResponseEntity.ok(byCustomerId.map(orderMapper::mapToResponse));
    }

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @PutMapping("/update-status-to-started")
    @Operation(summary = "Update status to started", description = "Update an order's status to started")
    public ResponseEntity<OrderFindResponse> updateStatusToStarted(@RequestParam Integer orderId) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Order order = orderService.updateStatusToStarted(orderId,principal.user().getId());
        return ResponseEntity.ok(orderMapper.mapToResponse(order));
    }

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @PutMapping("/update-status-to-done")
    @Operation(summary = "Update status to done", description = "Update an order's status to done")
    public ResponseEntity<OrderFindResponse> updateStatusToDone(@RequestParam Integer orderId) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Order order = orderService.updateStatusToDone(orderId,principal.user().getId());
        return ResponseEntity.ok(orderMapper.mapToResponse(order));
    }
}
