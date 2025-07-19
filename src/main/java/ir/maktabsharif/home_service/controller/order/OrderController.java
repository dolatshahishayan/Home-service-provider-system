package ir.maktabsharif.home_service.controller.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.dto.order.OrderFindResponse;
import ir.maktabsharif.home_service.dto.order.OrderSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.order.OrderSummaryDTO;
import ir.maktabsharif.home_service.mapper.order.OrderMapper;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
        Order saved = orderService.saveWithDTO(order);
        return ResponseEntity.ok(orderMapper.mapToResponse(saved));
    }

    @PutMapping("/update")
    @Operation(summary = "Update order", description = "Method for updating an order")
    public ResponseEntity<OrderFindResponse> update(@RequestBody @Validated(ValidationGroup.Update.class) OrderSaveUpdateRequest order) {
        Order updated = orderService.updateWithDTO(order);
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

    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @GetMapping("/find-all-by-expert")
    @Operation(summary = "Find all by expert", description = "Find all orders for an expert")
    public ResponseEntity<Page<OrderSummaryDTO>> findAllByExpert(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.findAllByExpertId(PageRequest.of(page, size)));
    }

    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @GetMapping("/find-order-with-details")
    @Operation(summary = "Find order with details", description = "Find order with details by order id")
    public ResponseEntity<OrderFindResponse> findOrderWithDetails(@RequestParam Integer orderId) {
        boolean isAccepted = orderService.existsByOrderIdAndExpertIdAndAcceptedTrue(orderId);
        if (!isAccepted) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        Order order = orderService.findById(orderId);
        return ResponseEntity.ok(orderMapper.mapToResponse(order));
    }

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @GetMapping("/find-all-by-customer")
    @Operation(summary = "Find all by customer", description = "Find all orders for a customer")
    public ResponseEntity<Page<OrderFindResponse>> findAllByCustomer(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<Order> byCustomerId = orderService.findByCustomerId(PageRequest.of(page, size));
        return ResponseEntity.ok(byCustomerId.map(orderMapper::mapToResponse));
    }

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @PutMapping("/update-status-to-started")
    @Operation(summary = "Update status to started", description = "Update an order's status to started")
    public ResponseEntity<OrderFindResponse> updateStatusToStarted(@RequestParam Integer orderId) {
        Order order = orderService.updateStatusToStarted(orderId);
        return ResponseEntity.ok(orderMapper.mapToResponse(order));
    }

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @PutMapping("/update-status-to-done")
    @Operation(summary = "Update status to done", description = "Update an order's status to done")
    public ResponseEntity<OrderFindResponse> updateStatusToDone(@RequestParam Integer orderId) {
        Order order = orderService.updateStatusToDone(orderId);
        return ResponseEntity.ok(orderMapper.mapToResponse(order));
    }
}
