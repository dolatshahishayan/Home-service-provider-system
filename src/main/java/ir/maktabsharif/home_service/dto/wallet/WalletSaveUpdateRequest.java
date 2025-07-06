package ir.maktabsharif.home_service.dto.wallet;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WalletSaveUpdateRequest {
    @NotNull(groups = {ValidationGroup.update.class})
    private Integer id;
    @NotNull(groups = {ValidationGroup.save.class})
    private Double balance;
    @NotNull(groups = {ValidationGroup.save.class})
    private Integer userId;
}
