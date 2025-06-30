package ir.maktabsharif.home_service.service.comment;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.comment.CommentSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.CouldNotUpdateException;
import ir.maktabsharif.home_service.exception.DuplicateInfoException;
import ir.maktabsharif.home_service.mapper.comment.CommentMapper;
import ir.maktabsharif.home_service.model.comment.Comment;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.repository.comment.CommentRepository;
import ir.maktabsharif.home_service.service.expert.ExpertService;
import ir.maktabsharif.home_service.service.order.OrderService;
import ir.maktabsharif.home_service.util.Session;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentServiceImpl extends BaseServiceImpl<Comment, CommentRepository, CommentMapper> implements CommentService {
    protected final OrderService orderService;
    protected final ExpertService expertService;

    public CommentServiceImpl(CommentRepository repository, CommentMapper mapper, OrderService orderService, ExpertService expertService) {
        super(repository, mapper);
        this.orderService = orderService;
        this.expertService = expertService;
    }
    @Override
    public void saveWithDTO(CommentSaveUpdateRequest commentSaveUpdateRequest) {
        Order order = orderService.findById(commentSaveUpdateRequest.getOrderId());
        String currentUserEmail = Session.getCurrentUser().getEmail();

        if (!order.getCustomer().getEmail().equals(currentUserEmail)) {
            throw new CouldNotUpdateException("You can't register any comments for this order!");
        }
        if (existsByOrder(order)) {
            throw new DuplicateInfoException("You have already registered a comment for this order!");
        }
        Comment comment = mapper.mapToEntity(commentSaveUpdateRequest);
        comment.setRegistrationDate(LocalDateTime.now());
        save(comment);

        Expert expert = order.getExpert();
        Double commentScore = comment.getExpertScore();
        Double expertScore = expert.getScore();
        Double finalScore = (expertScore + commentScore) / 2;
        expert.setScore(finalScore);
        expertService.update(expert);
    }

    @Override
    public boolean existsByOrder(Order order) {
        return repository.existsByOrder(order);
    }

    @Override
    public Comment findByOrder(Order order) {
        return repository.findByOrder(order);
    }

    @Override
    public double viewExpertScoreByOrder(Integer orderId) {
        Order order = orderService.findById(orderId);
        Comment byOrder = findByOrder(order);
        return byOrder.getExpertScore();
    }
}
