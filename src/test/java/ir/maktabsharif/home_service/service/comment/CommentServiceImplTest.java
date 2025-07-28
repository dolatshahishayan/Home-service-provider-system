package ir.maktabsharif.home_service.service.comment;

import ir.maktabsharif.home_service.dto.comment.CommentSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.CouldNotUpdateException;
import ir.maktabsharif.home_service.exception.DuplicateInfoException;
import ir.maktabsharif.home_service.exception.InvalidRequestException;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import ir.maktabsharif.home_service.mapper.comment.CommentMapper;
import ir.maktabsharif.home_service.model.comment.Comment;
import ir.maktabsharif.home_service.model.enums.ExpertStatus;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.repository.comment.CommentRepository;
import ir.maktabsharif.home_service.service.expert.ExpertService;
import ir.maktabsharif.home_service.service.order.OrderService;
import ir.maktabsharif.home_service.service.suggestion.SuggestionService;
import ir.maktabsharif.home_service.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private OrderService orderService;
    @Mock
    private ExpertService expertService;
    @Mock
    private UserService userService;
    @Mock
    private SuggestionService suggestionService;

    private CommentSaveUpdateRequest request;
    private User user;
    private Expert expert;
    private Order order;
    private Suggestion suggestion;
    private Comment comment;

    @BeforeEach
    void setUp() {
        request = new CommentSaveUpdateRequest();
        request.setOrderId(1);
        request.setExpertScore(4.0);

        expert = new Expert();
        expert.setId(10);
        expert.setScore(5.0);
        expert.setExpertStatus(ExpertStatus.VERIFIED);
        user = new User();
        user.setId(100);
        user.setEmail("customer@example.com");
        Customer customer = new Customer();
        customer.setId(100);
        customer.setEmail("customer@example.com");

        order = new Order();
        order.setId(1);
        order.setCustomer(customer);
        order.setOrderStatus(OrderStatus.PAYED);
        order.setExpert(expert);

        suggestion = new Suggestion();
        suggestion.setId(1);

        comment = new Comment();
        comment.setOrder(order);
        comment.setExpertScore(4.0);
    }

    @Test
    void saveWithDTO_shouldSaveSuccessfully() {
        when(orderService.findById(1)).thenReturn(order);
        when(userService.findById(100)).thenReturn(user);
        when(commentRepository.existsByOrder(order)).thenReturn(false);
        when(suggestionService.findByOrderId(1)).thenReturn(suggestion);
        when(orderService.reduce1ScoreFromExpertPerHour(suggestion)).thenReturn(0L);
        when(commentMapper.mapToEntity(request)).thenReturn(comment);
        when(expertService.save(any(Expert.class))).thenReturn(expert);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment result = commentService.saveWithDTO(request, 100);

        assertNotNull(result);
        verify(expertService).save(expert);
        verify(commentRepository).save(comment);
    }

    @Test
    void saveWithDTO_shouldThrowException_whenUserIsNotOwner() {
        user.setEmail("another@example.com");
        when(orderService.findById(1)).thenReturn(order);
        when(userService.findById(100)).thenReturn(user);

        assertThrows(CouldNotUpdateException.class, () -> commentService.saveWithDTO(request, 100));
    }

    @Test
    void saveWithDTO_shouldThrowException_whenCommentExists() {
        when(orderService.findById(1)).thenReturn(order);
        when(userService.findById(100)).thenReturn(user);
        when(commentRepository.existsByOrder(order)).thenReturn(true);

        assertThrows(DuplicateInfoException.class, () -> commentService.saveWithDTO(request, 100));
    }

    @Test
    void saveWithDTO_shouldThrowException_whenOrderNotPayed() {
        order.setOrderStatus(OrderStatus.STARTED);
        when(orderService.findById(1)).thenReturn(order);
        when(userService.findById(100)).thenReturn(user);

        assertThrows(CouldNotUpdateException.class, () -> commentService.saveWithDTO(request, 100));
    }

    @Test
    void existsByOrder_shouldReturnTrue() {
        when(orderService.findById(1)).thenReturn(order);
        when(commentRepository.existsByOrder(order)).thenReturn(true);

        assertTrue(commentService.existsByOrder(1));
    }

    @Test
    void findByOrder_shouldReturnComment() {
        when(orderService.findById(1)).thenReturn(order);
        when(commentRepository.findByOrder(order)).thenReturn(Optional.of(comment));

        Comment result = commentService.findByOrder(1);
        assertEquals(comment, result);
    }

    @Test
    void findByOrder_shouldThrowException_whenNotFound() {
        when(orderService.findById(1)).thenReturn(order);
        when(commentRepository.findByOrder(order)).thenReturn(Optional.empty());

        assertThrows(NoElementFoundException.class, () -> commentService.findByOrder(1));
    }

    @Test
    void viewExpertScoreByOrder_shouldReturnScore() {
        order.setExpert(expert);
        when(orderService.findById(1)).thenReturn(order);
        when(commentRepository.findByOrder(order)).thenReturn(Optional.of(comment));

        double score = commentService.viewExpertScoreByOrder(1);
        assertEquals(4.0, score);
    }

    @Test
    void viewExpertScoreByOrder_shouldThrow_whenExpertNotVerified() {
        expert.setExpertStatus(ExpertStatus.NEW);
        order.setExpert(expert);
        when(orderService.findById(1)).thenReturn(order);
        when(commentRepository.findByOrder(order)).thenReturn(Optional.of(comment));

        assertThrows(InvalidRequestException.class, () -> commentService.viewExpertScoreByOrder(1));
    }

    @Test
    void viewExpertAverageScore_shouldReturnScore() {
        user.setId(10);
        when(userService.findById(10)).thenReturn(user);
        when(expertService.findById(10)).thenReturn(expert);

        double result = commentService.viewExpertAverageScore(10);
        assertEquals(5.0, result);
    }

    @Test
    void viewExpertAverageScore_shouldThrow_whenNotVerified() {
        expert.setExpertStatus(ExpertStatus.NEW);
        when(userService.findById(10)).thenReturn(user);
        when(expertService.findById(anyInt())).thenReturn(expert);

        assertThrows(InvalidRequestException.class, () -> commentService.viewExpertAverageScore(10));
    }
}

