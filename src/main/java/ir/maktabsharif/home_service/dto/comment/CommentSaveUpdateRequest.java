package ir.maktabsharif.home_service.dto.comment;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.dto.order.OrderFindResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentSaveUpdateRequest {
    @NotBlank(groups = {ValidationGroup.update.class})
    private Integer id;
    private String context;
    @NotNull
    private Double expertScore;
    @NotBlank(groups = {ValidationGroup.update.class,ValidationGroup.save.class})
    private OrderFindResponse order;

}
