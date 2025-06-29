package ir.maktabsharif.home_service.dto.suggestion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionFindResponse {
    private Integer id;
    private Integer expertId;
    private Integer orderId;
    private LocalDateTime creationDate;
    private String description;
    private Double price;
    private Double workDuration;
    private LocalDateTime startDate;
    private Boolean accepted;
}
