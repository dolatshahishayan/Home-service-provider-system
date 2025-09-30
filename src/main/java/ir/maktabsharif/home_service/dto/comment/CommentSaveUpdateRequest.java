package ir.maktabsharif.home_service.dto.comment;

import ir.maktabsharif.home_service.dto.ValidationGroup;

import javax.validation.constraints.*;


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

    public CommentSaveUpdateRequest() {
    }

    public CommentSaveUpdateRequest(Integer id, String context, Double expertScore, Integer orderId) {
        this.id = id;
        this.context = context;
        this.expertScore = expertScore;
        this.orderId = orderId;
    }

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
