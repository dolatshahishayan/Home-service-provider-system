
package ir.maktabsharif.home_service.service.comment;

import ir.maktabsharif.home_service.dto.comment.CommentSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.user.UserSessionDTO;
import ir.maktabsharif.home_service.exception.CouldNotUpdateException;
import ir.maktabsharif.home_service.exception.DuplicateInfoException;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import ir.maktabsharif.home_service.mapper.comment.CommentMapper;
import ir.maktabsharif.home_service.model.comment.Comment;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.repository.comment.CommentRepository;
import ir.maktabsharif.home_service.service.expert.ExpertService;
import ir.maktabsharif.home_service.service.order.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private OrderService orderService;

    @Mock
    private ExpertService expertService;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void saveWithDTO_shouldSaveNewExpertScore_WhenScoreIsNull() {
        CommentSaveUpdateRequest dto = new CommentSaveUpdateRequest();
        dto.setOrderId(1);
        dto.setExpertScore(5.0);

        UserSessionDTO user = new UserSessionDTO();
        user.setEmail("customer@example.com");

        Customer customer = new Customer();
        customer.setEmail("customer@example.com");

        Expert expert = new Expert();
        expert.setScore(null);

        Order order = new Order();
        order.setId(1);
        order.setOrderStatus(OrderStatus.DONE);
        order.setCustomer(customer);
        order.setExpert(expert);

        Comment comment = new Comment();
        comment.setExpertScore(5.0);

        when(orderService.findById(1)).thenReturn(order);
        when(commentRepository.existsByOrder(order)).thenReturn(false);
        when(commentMapper.mapToEntity(dto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);

        Comment saved = commentService.saveWithDTO(dto, user);

        assertEquals(order, saved.getOrder());
        assertEquals(5.0, expert.getScore());
        assertNotNull(saved.getRegistrationDate());

        verify(expertService).save(expert);
        verify(commentRepository).save(comment);
    }

    @Test
    void saveWithDTO_shouldThrow_WhenOrderStatusNotDoneOrPayed() {
        CommentSaveUpdateRequest dto = new CommentSaveUpdateRequest();
        dto.setOrderId(1);
        dto.setExpertScore(4.0);

        UserSessionDTO user = new UserSessionDTO();
        user.setEmail("customer@example.com");

        Customer customer = new Customer();
        customer.setEmail("customer@example.com");

        Order order = new Order();
        order.setId(1);
        order.setOrderStatus(OrderStatus.WAITING_FOR_EXPERT_SUGGESTION);
        order.setCustomer(customer);

        when(orderService.findById(1)).thenReturn(order);

        assertThrows(CouldNotUpdateException.class, () ->
                commentService.saveWithDTO(dto, user));
    }

    @Test
    void findByOrder_shouldThrow_WhenNotExists() {
        Order order = new Order();
        order.setId(1);

        when(orderService.findById(1)).thenReturn(order);
        when(commentRepository.findByOrder(order)).thenReturn(Optional.empty());

        assertThrows(NoElementFoundException.class, () ->
                commentService.findByOrder(1));
    }

    @Test
    void viewExpertAverageScore_shouldReturnScore() {
        Expert expert = new Expert();
        expert.setId(1);
        expert.setScore(4.2);

        when(expertService.findById(1)).thenReturn(expert);

        double score = commentService.viewExpertAverageScore(1);
        assertEquals(4.2, score);
    }

    @Test
    void viewExpertScoreByOrder_shouldReturnCommentScore() {
        Order order = new Order();
        order.setId(1);

        Comment comment = new Comment();
        comment.setExpertScore(3.3);

        when(orderService.findById(1)).thenReturn(order);
        when(commentRepository.findByOrder(order)).thenReturn(Optional.of(comment));

        double score = commentService.viewExpertScoreByOrder(1);
        assertEquals(3.3, score);
    }

    @Test
    void saveWithDTO_shouldUpdateExpertScore_WhenAlreadyExists() {
        CommentSaveUpdateRequest dto = new CommentSaveUpdateRequest();
        dto.setOrderId(1);
        dto.setExpertScore(4.0);

        UserSessionDTO user = new UserSessionDTO();
        user.setEmail("customer@example.com");

        Customer customer = new Customer();
        customer.setEmail("customer@example.com");

        Expert expert = new Expert();
        expert.setScore(4.0);

        Order order = new Order();
        order.setId(1);
        order.setOrderStatus(OrderStatus.DONE);
        order.setCustomer(customer);
        order.setExpert(expert);

        Comment comment = new Comment();
        comment.setExpertScore(4.0);

        when(orderService.findById(1)).thenReturn(order);
        when(commentRepository.existsByOrder(order)).thenReturn(false);
        when(commentMapper.mapToEntity(dto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);

        Comment saved = commentService.saveWithDTO(dto, user);

        assertEquals(4.0, expert.getScore());
        assertNotNull(saved.getRegistrationDate());
        verify(expertService).save(expert);
        verify(commentRepository).save(comment);
    }

    @Test
    void existsByOrder_shouldReturnCorrectValue() {
        Order order = new Order();
        order.setId(1);

        when(orderService.findById(1)).thenReturn(order);
        when(commentRepository.existsByOrder(order)).thenReturn(true);
        assertTrue(commentService.existsByOrder(1));

        when(commentRepository.existsByOrder(order)).thenReturn(false);
        assertFalse(commentService.existsByOrder(1));
    }

    @Test
    void saveWithDTO_shouldThrow_WhenCustomerIsNotOwner() {
        CommentSaveUpdateRequest dto = new CommentSaveUpdateRequest();
        dto.setOrderId(1);

        UserSessionDTO user = new UserSessionDTO();
        user.setEmail("other@example.com");

        Customer customer = new Customer();
        customer.setEmail("owner@example.com");

        Order order = new Order();
        order.setCustomer(customer);

        when(orderService.findById(1)).thenReturn(order);

        assertThrows(CouldNotUpdateException.class, () ->
                commentService.saveWithDTO(dto, user));
    }

    @Test
    void saveWithDTO_shouldThrow_WhenCommentAlreadyExists() {
        CommentSaveUpdateRequest dto = new CommentSaveUpdateRequest();
        dto.setOrderId(1);

        UserSessionDTO user = new UserSessionDTO();
        user.setEmail("owner@example.com");

        Customer customer = new Customer();
        customer.setEmail("owner@example.com");

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderStatus(OrderStatus.PAYED);
        order.setId(1);

        when(orderService.findById(1)).thenReturn(order);
        when(commentRepository.existsByOrder(any(Order.class))).thenReturn(true);

        assertThrows(DuplicateInfoException.class, () ->
                commentService.saveWithDTO(dto, user));
    }
}
