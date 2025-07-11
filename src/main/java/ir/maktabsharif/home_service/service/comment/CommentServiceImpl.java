package ir.maktabsharif.home_service.service.comment;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.comment.CommentSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.user.UserSessionDTO;
import ir.maktabsharif.home_service.exception.CouldNotUpdateException;
import ir.maktabsharif.home_service.exception.DuplicateInfoException;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import ir.maktabsharif.home_service.mapper.comment.CommentMapper;
import ir.maktabsharif.home_service.model.comment.Comment;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.repository.comment.CommentRepository;
import ir.maktabsharif.home_service.service.expert.ExpertService;
import ir.maktabsharif.home_service.service.order.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
public class CommentServiceImpl extends BaseServiceImpl<Comment, Integer, CommentRepository, CommentMapper> implements CommentService {
    protected final OrderService orderService;
    protected final ExpertService expertService;

    public CommentServiceImpl(CommentRepository repository, CommentMapper commentMapper, OrderService orderService, ExpertService expertService) {
        super(repository, commentMapper);
        this.orderService = orderService;
        this.expertService = expertService;
    }

    @Override
    public Comment saveWithDTO(CommentSaveUpdateRequest commentSaveUpdateRequest, UserSessionDTO currentUser) {
        Order order = orderService.findById(commentSaveUpdateRequest.getOrderId());
        String currentUserEmail = currentUser.getEmail();

        if (!order.getCustomer().getEmail().equals(currentUserEmail)) {
            throw new CouldNotUpdateException("You can't register any comments for this order!");
        }
        if (existsByOrder(order.getId())) {
            throw new DuplicateInfoException("You have already registered a comment for this order!");
        }

        if (order.getOrderStatus()!= OrderStatus.PAYED&&order.getOrderStatus()!=OrderStatus.DONE) {
            throw new CouldNotUpdateException("You can't register any comments for this order because the order has not been finished yet!");
        }

        Comment comment = mapper.mapToEntity(commentSaveUpdateRequest);
        Expert expert = order.getExpert();
        Double commentScore = comment.getExpertScore();
        if (expert.getScore() != null) {
            Double expertScore = expert.getScore();
            Double finalScore = (expertScore + commentScore) / 2;
            expert.setScore(finalScore);
            expertService.save(expert);
        } else {
            expert.setScore(commentScore);
            expertService.save(expert);
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
        return byOrder.getExpertScore();
    }

    @Override
    public double viewExpertAverageScore(Integer expertId){
        Expert expert = expertService.findById(expertId);
        return expert.getScore();
    }
}
