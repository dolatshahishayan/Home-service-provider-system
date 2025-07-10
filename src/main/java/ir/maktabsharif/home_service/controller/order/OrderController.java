package ir.maktabsharif.home_service.controller.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.dto.order.OrderFindResponse;
import ir.maktabsharif.home_service.dto.order.OrderSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.user.UserSessionDTO;
import ir.maktabsharif.home_service.mapper.order.OrderMapper;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.service.order.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@Tag(name = "Order controller", description = "Controller class for order")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping("/save")
    @Operation(summary = "Save order", description = "Method for saving an order")
    public ResponseEntity<OrderFindResponse> save(@RequestBody @Validated(ValidationGroup.Save.class) OrderSaveUpdateRequest order, HttpSession session) {
        UserSessionDTO currentUser = (UserSessionDTO) session.getAttribute("currentUser");
        Order saved = orderService.saveWithDTO(order,currentUser.getUserId());
        return ResponseEntity.ok(orderMapper.mapToResponse(saved));
    }

    @PutMapping("/update")
    @Operation(summary = "Update order", description = "Method for updating an order")
    public ResponseEntity<?> update(@RequestBody @Validated(ValidationGroup.Update.class) OrderSaveUpdateRequest order, HttpSession session) {
        UserSessionDTO currentUser = (UserSessionDTO) session.getAttribute("currentUser");
        if (currentUser==null) {
            return new ResponseEntity<>("No user logged in",HttpStatus.UNAUTHORIZED);
        }
        Order updated = orderService.updateWithDTO(order,currentUser.getUserId());
        return ResponseEntity.ok(orderMapper.mapToResponse(updated));
    }

    @GetMapping("/exists-by-expert-and-order-status-in")
    @Operation(summary = "Exists by expert and order status in", description = "Checks if an expert has an order with given statuses")
    public ResponseEntity<Boolean> existsByExpertAndOrderStatusIn(@RequestParam Integer expertId, @RequestParam List<OrderStatus> orderStatuses) {
        return ResponseEntity.ok(orderService.existsBySpecialistAndOrderStatusIn(expertId, orderStatuses));
    }

    @GetMapping("/find-by-service-id")
    @Operation(summary = "Find by service id", description = "Finds orders by service id")
    public ResponseEntity<List<OrderFindResponse>> findByServiceId(@RequestParam Integer serviceId) {
        List<Order> orders = orderService.findByServiceId(serviceId);
        List<OrderFindResponse> responses = new ArrayList<>();
        for (Order order : orders) {
            responses.add(orderMapper.mapToResponse(order));
        }
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/choose-expert")
    @Operation(summary = "Choose expert", description = "Method for choosing an expert for an order")
    public ResponseEntity<String> chooseExpert(@RequestParam Integer suggestionId) {
        orderService.chooseExpert(suggestionId);
        return ResponseEntity.ok("Expert chosen");
    }

    @GetMapping("/find-all-by-expert-id")
    @Operation(summary = "Find all by expert id",description = "Find all orders for an expert")
    public ResponseEntity<?> findAllByExpertId(HttpSession session) {
        UserSessionDTO currentUser=(UserSessionDTO) session.getAttribute("currentUser");
        if (currentUser==null) {
            return new ResponseEntity<>("No user logged in",HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(orderService.findAllByExpertId(currentUser.getUserId()));
    }

    @GetMapping("/find-order-with-details")
    @Operation(summary = "Find order with details",description = "Find order with details by order id")
    public ResponseEntity<?> findOrderWithDetails(@RequestParam Integer orderId,HttpSession session) {
        UserSessionDTO currentUser=(UserSessionDTO) session.getAttribute("currentUser");
        if (currentUser==null) {
            return new ResponseEntity<>("No user logged in",HttpStatus.UNAUTHORIZED);
        }
        boolean isAccepted = orderService.existsByOrderIdAndExpertIdAndAcceptedTrue(orderId, currentUser.getUserId());
        if (!isAccepted) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        Order order = orderService.findById(orderId);
        return ResponseEntity.ok(orderMapper.mapToResponse(order));
    }


    @GetMapping("/find-all-by-customer-id")
    @Operation(summary = "Find all by customer id",description = "Find all orders for a customer")
    public ResponseEntity<?> findAllByCustomerId(HttpSession session) {
        UserSessionDTO currentUser=(UserSessionDTO) session.getAttribute("currentUser");
        System.out.println("Session ID: " + session.getId());
        System.out.println("User in session: " + session.getAttribute("currentUser"));
        if (currentUser==null) {
            return new ResponseEntity<>("No user logged in",HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(orderService.findByCustomerId(currentUser.getUserId()));
    }

    @PutMapping("/update-status-to-started")
    @Operation(summary = "Update status to started",description = "Update an order's status to started")
    public ResponseEntity<?> updateStatusToStarted(@RequestParam Integer orderId, HttpSession session) {
        UserSessionDTO currentUser = (UserSessionDTO) session.getAttribute("currentUser");
        if (currentUser == null) {
            return new ResponseEntity<>("No user logged in",HttpStatus.UNAUTHORIZED);
        }
        Order order = orderService.updateStatusToStarted(orderId, currentUser);
        return ResponseEntity.ok(orderMapper.mapToResponse(order));
    }

    @PutMapping("/update-status-to-done")
    @Operation(summary = "Update status to done",description = "Update an order's status to done")
    public ResponseEntity<OrderFindResponse> updateStatusToDone(@RequestParam Integer orderId, HttpSession session) {
        UserSessionDTO currentUser = (UserSessionDTO) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Order order = orderService.updateStatusToDone(orderId, currentUser);
        return ResponseEntity.ok(orderMapper.mapToResponse(order));
    }
}
