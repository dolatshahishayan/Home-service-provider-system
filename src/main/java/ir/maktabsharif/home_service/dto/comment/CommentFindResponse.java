package ir.maktabsharif.home_service.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentFindResponse {
    private Integer id;
    private String context;
    private Double expertScore;
    private Integer orderId;


}
