package ir.maktabsharif.home_service.service.comment;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
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
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.repository.comment.CommentRepository;
import ir.maktabsharif.home_service.service.expert.ExpertService;
import ir.maktabsharif.home_service.service.order.OrderService;
import ir.maktabsharif.home_service.service.suggestion.SuggestionService;
import ir.maktabsharif.home_service.service.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional
public class CommentServiceImpl extends BaseServiceImpl<Comment, Integer, CommentRepository, CommentMapper> implements CommentService {
    protected final OrderService orderService;
    protected final ExpertService expertService;
    private final UserService userService;
    private final SuggestionService suggestionService;

    public CommentServiceImpl(CommentRepository repository, CommentMapper commentMapper, OrderService orderService, ExpertService expertService, UserService userService, SuggestionService suggestionService) {
        super(repository, commentMapper);
        this.orderService = orderService;
        this.expertService = expertService;
        this.userService = userService;
        this.suggestionService = suggestionService;
    }

    @Override
    public Comment saveWithDTO(CommentSaveUpdateRequest commentSaveUpdateRequest,Integer userId) {
        Order order = orderService.findById(commentSaveUpdateRequest.getOrderId());
        User currentUser = userService.findById(userId);
        String currentUserEmail = currentUser.getEmail();
        if (!order.getCustomer().getEmail().equals(currentUserEmail)) {
            throw new CouldNotUpdateException("You can't register any comments for this order!");
        }
        if (existsByOrder(order.getId())) {
            throw new DuplicateInfoException("You have already registered a comment for this order!");
        }

        if (order.getOrderStatus() != OrderStatus.PAYED) {
            throw new CouldNotUpdateException("You can't register any comments for this order because the order has not been finished yet!");
        }
        return setExpertScore(order, commentSaveUpdateRequest);
    }

    private Comment setExpertScore(Order order, CommentSaveUpdateRequest commentSaveUpdateRequest) {
        Suggestion byOrderId = suggestionService.findByOrderId(order.getId());
        long between = orderService.reduce1ScoreFromExpertPerHour(byOrderId);
        Comment comment = mapper.mapToEntity(commentSaveUpdateRequest);
        Expert expert = order.getExpert();
        Double commentScore = comment.getExpertScore().doubleValue();
        Double finalScore = commentScore - between;
        setExpertScore(expert, finalScore);
        Expert saved = expertService.save(expert);
        return saveComment(order, saved, comment);
    }

    private static void setExpertScore(Expert expert, Double finalScore) {
        if (expert.getScore() != null) {
            Double expertScore = expert.getScore().doubleValue();
            Double finalExpertScore = (expertScore + finalScore) / 2;
            expert.setScore(BigDecimal.valueOf(finalExpertScore));
        } else {
            expert.setScore(BigDecimal.valueOf(finalScore));
        }
    }

    private Comment saveComment(Order order, Expert expert, Comment comment) {
        if (expert.getScore().doubleValue() < 0) {
            expertService.updateStatusToUnverified(expert.getId());
        }
        comment.setOrder(order);
        comment.setRegistrationDate(LocalDateTime.now());
        return save(comment);
    }

    @Override
    public boolean existsByOrder(Integer orderId) {
        Order order = orderService.findById(orderId);
        return repository.existsByOrder(order);
    }

    @Override
    public Comment findByOrder(Integer orderId) {
        Order order = orderService.findById(orderId);
        return repository.findByOrder(order).orElseThrow(NoElementFoundException::new);

    }

    @Override
    public double viewExpertScoreByOrder(Integer orderId) {
        Comment byOrder = findByOrder(orderId);
        if (byOrder.getOrder().getExpert() != null) {
            if (byOrder.getOrder().getExpert().getExpertStatus() != ExpertStatus.VERIFIED) {
                throw new InvalidRequestException("Expert status must be VERIFIED");
            }
        }
        return byOrder.getExpertScore().doubleValue();
    }

    @Override
    public double viewExpertAverageScore(Integer userId) {
        User currentUser = (userService.findById(userId));
        Expert expert = expertService.findById(currentUser.getId());
        if (expert.getExpertStatus() != ExpertStatus.VERIFIED) {
            throw new InvalidRequestException("Expert status must be VERIFIED");
        }
        return expert.getScore().doubleValue();
    }
}
