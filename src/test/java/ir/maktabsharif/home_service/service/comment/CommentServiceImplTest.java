package ir.maktabsharif.home_service.service.comment;

import ir.maktabsharif.home_service.dto.comment.CommentSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.user.UserSessionDTO;
import ir.maktabsharif.home_service.exception.CouldNotUpdateException;
import ir.maktabsharif.home_service.exception.DuplicateInfoException;
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

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    void saveWithDTO_shouldThrow_WhenUserIsNotCustomer() {
        CommentSaveUpdateRequest dto = new CommentSaveUpdateRequest();
        dto.setOrderId(1);

        UserSessionDTO userSessionDTO = new UserSessionDTO();
        userSessionDTO.setEmail("unauthorized@example.com");

        Customer customer = new Customer();
        customer.setEmail("realcustomer@example.com");

        Order order = new Order();
        order.setCustomer(customer);

        when(orderService.findById(1)).thenReturn(order);

        assertThrows(CouldNotUpdateException.class, () ->
                commentService.saveWithDTO(dto, userSessionDTO)
        );
    }

    @Test
    void saveWithDTO_shouldThrow_WhenCommentAlreadyExists() {
        CommentSaveUpdateRequest dto = new CommentSaveUpdateRequest();
        dto.setOrderId(1);

        UserSessionDTO userSessionDTO = new UserSessionDTO();
        userSessionDTO.setEmail("user@example.com");

        Customer customer = new Customer();
        customer.setEmail("user@example.com");

        Order order = new Order();
        order.setCustomer(customer);
        order.setId(1);
        order.setOrderStatus(OrderStatus.DONE);
        when(orderService.findById(1)).thenReturn(order);
        when(commentRepository.existsByOrder(order)).thenReturn(true);

        assertThrows(DuplicateInfoException.class, () ->
                commentService.saveWithDTO(dto, userSessionDTO)
        );
    }

    @Test
    void saveWithDTO_shouldSaveCommentAndUpdateExpertScore() {
        CommentSaveUpdateRequest dto = new CommentSaveUpdateRequest();
        dto.setOrderId(1);
        dto.setExpertScore(5.0);

        UserSessionDTO userSessionDTO = new UserSessionDTO();
        userSessionDTO.setEmail("customer@example.com");

        Customer customer = new Customer();
        customer.setEmail("customer@example.com");

        Expert expert = new Expert();
        expert.setScore(3.0);

        Order order = new Order();
        order.setId(1);
        order.setCustomer(customer);
        order.setExpert(expert);
        order.setOrderStatus(OrderStatus.DONE);
        Comment comment = new Comment();
        comment.setExpertScore(5.0);

        when(orderService.findById(1)).thenReturn(order);
        when(commentRepository.existsByOrder(order)).thenReturn(false);
        when(commentMapper.mapToEntity(dto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);

        Comment saved = commentService.saveWithDTO(dto, userSessionDTO);

        assertEquals(order, saved.getOrder());
        assertEquals(4.0, expert.getScore());
        assertNotNull(saved.getRegistrationDate());

        verify(expertService).save(expert);
        verify(commentRepository).save(comment);
    }

    @Test
    void existsByOrder_shouldReturnTrueOrFalse() {
        Order order = new Order();
        order.setId(1);

        when(orderService.findById(1)).thenReturn(order);
        when(commentRepository.existsByOrder(order)).thenReturn(true);

        assertTrue(commentService.existsByOrder(1));

        when(commentRepository.existsByOrder(order)).thenReturn(false);
        assertFalse(commentService.existsByOrder(1));
    }

    @Test
    void findByOrder_shouldReturnComment_whenExists() {
        Order order = new Order();
        order.setId(1);

        Comment comment = new Comment();

        when(orderService.findById(1)).thenReturn(order);
        when(commentRepository.findByOrder(order)).thenReturn(Optional.of(comment));

        Comment result = commentService.findByOrder(1);

        assertSame(comment, result);
    }

    @Test
    void viewExpertScoreByOrder_shouldReturnExpertScore() {
        Order order = new Order();
        order.setId(1);
        Comment comment = new Comment();
        comment.setExpertScore(4.5);

        when(orderService.findById(1)).thenReturn(order);
        when(commentRepository.findByOrder(order)).thenReturn(Optional.of(comment));

        double score = commentService.viewExpertScoreByOrder(1);

        assertEquals(4.5, score);
    }
}