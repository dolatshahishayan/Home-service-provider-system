package ir.maktabsharif.home_service.dto.comment;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentSaveUpdateRequest {
    @NotNull(groups = {ValidationGroup.Update.class})
    private Integer id;
    private String context;
    @NotNull(groups = {ValidationGroup.Save.class})
    @Min(1)
    @Max(5)
    private Double expertScore;
    @NotNull
    private Integer orderId;


}
