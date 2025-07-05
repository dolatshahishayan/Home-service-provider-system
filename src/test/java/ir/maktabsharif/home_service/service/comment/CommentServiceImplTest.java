package ir.maktabsharif.home_service.service.comment;

import ir.maktabsharif.home_service.dto.comment.CommentSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.user.UserSessionDTO;
import ir.maktabsharif.home_service.exception.CouldNotUpdateException;
import ir.maktabsharif.home_service.exception.DuplicateInfoException;
import ir.maktabsharif.home_service.mapper.comment.CommentMapper;
import ir.maktabsharif.home_service.model.comment.Comment;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
    @Mock
    private OrderService orderService;

    @Mock
    private ExpertService expertService;

    @Mock
    private CommentRepository repository;

    @Mock
    private CommentMapper mapper;

    @InjectMocks
    private CommentServiceImpl commentService;


    @Test
    void saveWithDTO_ShouldThrowException_WhenUserIsNotCustomerOfOrder() {
        CommentSaveUpdateRequest dto = new CommentSaveUpdateRequest();
        dto.setOrderId(1);
        UserSessionDTO userSessionDTO = new UserSessionDTO();
        Order order = new Order();
        Customer otherCustomer = new Customer();
        otherCustomer.setEmail("test123");
        order.setCustomer(otherCustomer);

        when(orderService.findById(dto.getOrderId())).thenReturn(order);

        assertThrows(CouldNotUpdateException.class, () -> commentService.saveWithDTO(dto,userSessionDTO));
    }

    @Test
    void saveWithDTO_ShouldThrowException_WhenCommentAlreadyExists() {
        CommentSaveUpdateRequest dto = new CommentSaveUpdateRequest();
        dto.setOrderId(1);

        Order order = new Order();
        Customer customer = new Customer();
        customer.setEmail("test");
        order.setCustomer(customer);

        UserSessionDTO userSessionDTO = new UserSessionDTO();

        when(orderService.findById(dto.getOrderId())).thenReturn(order);
        when(commentService.existsByOrder(order)).thenReturn(true);

        assertThrows(DuplicateInfoException.class, () -> commentService.saveWithDTO(dto, userSessionDTO));
    }

    @Test
    void saveWithDTO_ShouldSaveCommentAndUpdateExpertScore() {
        CommentSaveUpdateRequest dto = new CommentSaveUpdateRequest();
        dto.setOrderId(1);

        Customer customer = new Customer();
        customer.setEmail("test");
        Expert expert = new Expert();
        expert.setScore(4.0);

        Order order = new Order();
        order.setCustomer(customer);
        order.setExpert(expert);

        Comment comment = new Comment();
        comment.setExpertScore(5.0);
        UserSessionDTO userSessionDTO = new UserSessionDTO();
        when(orderService.findById(dto.getOrderId())).thenReturn(order);
        when(commentService.existsByOrder(order)).thenReturn(false);
        when(mapper.mapToEntity(dto)).thenReturn(comment);

        commentService.saveWithDTO(dto,userSessionDTO);

        verify(repository).save(comment);
        assertEquals(4.5, expert.getScore());
        verify(expertService).save(expert);
    }

    @Test
    void existsByOrder_ShouldReturnTrue_WhenRepositorySaysExists() {
        Order order = new Order();

        when(repository.existsByOrder(order)).thenReturn(true);

        boolean result = commentService.existsByOrder(order);
        assertTrue(result);
    }

    @Test
    void findByOrder_ShouldReturnComment_WhenExists() {
        Order order = new Order();
        Comment expectedComment = new Comment();

        when(repository.findByOrder(order)).thenReturn(Optional.of(expectedComment));

        Comment result = commentService.findByOrder(order);
        assertEquals(expectedComment, result);
    }

    @Test
    void viewExpertScoreByOrder_ShouldReturnExpertScore() {
        int orderId = 1;
        Order order = new Order();
        Comment comment = new Comment();
        comment.setExpertScore(4.7);

        when(orderService.findById(orderId)).thenReturn(order);
        when(repository.findByOrder(order)).thenReturn(Optional.of(comment));

        double result = commentService.viewExpertScoreByOrder(orderId);
        assertEquals(4.7, result);
    }
}