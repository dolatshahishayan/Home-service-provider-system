package ir.maktabsharif.home_service.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



public class CommentFindResponse {
    private Integer id;
    private String context;
    private Double expertScore;
    private Integer orderId;

    public CommentFindResponse() {
    }

    public CommentFindResponse(Integer id, String context, Double expertScore, Integer orderId) {
        this.id = id;
        this.context = context;
        this.expertScore = expertScore;
        this.orderId = orderId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Double getExpertScore() {
        return expertScore;
    }

    public void setExpertScore(Double expertScore) {
        this.expertScore = expertScore;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
}
