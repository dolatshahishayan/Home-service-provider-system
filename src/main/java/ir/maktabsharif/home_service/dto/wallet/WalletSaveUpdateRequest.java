package ir.maktabsharif.home_service.dto.wallet;

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

public class WalletSaveUpdateRequest {
    @NotNull(groups = {ValidationGroup.Update.class})
    private Integer id;
    @NotNull(groups = {ValidationGroup.Save.class})
    private Double balance;
    @NotNull(groups = {ValidationGroup.Save.class})
    private Integer userId;

}
