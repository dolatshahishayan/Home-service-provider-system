package ir.maktabsharif.home_service.dto.comment;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public @NotNull(groups = {ValidationGroup.Update.class}) Integer getId() {
        return id;
    }

    public void setId(@NotNull(groups = {ValidationGroup.Update.class}) Integer id) {
        this.id = id;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public @NotNull(groups = {ValidationGroup.Save.class}) @Min(1) @Max(5) Double getExpertScore() {
        return expertScore;
    }

    public void setExpertScore(@NotNull(groups = {ValidationGroup.Save.class}) @Min(1) @Max(5) Double expertScore) {
        this.expertScore = expertScore;
    }

    public @NotNull Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(@NotNull Integer orderId) {
        this.orderId = orderId;
    }
}
