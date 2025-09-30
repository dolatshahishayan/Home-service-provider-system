package ir.maktabsharif.home_service.dto.wallet;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
public class WalletSaveUpdateRequest {
    @NotNull(groups = {ValidationGroup.Update.class})
    private Integer id;
    @NotNull(groups = {ValidationGroup.Save.class})
    private Double balance;
    @NotNull(groups = {ValidationGroup.Save.class})
    private Integer userId;

    public @NotNull(groups = {ValidationGroup.Update.class}) Integer getId() {
        return id;
    }

    public void setId(@NotNull(groups = {ValidationGroup.Update.class}) Integer id) {
        this.id = id;
    }

    public @NotNull(groups = {ValidationGroup.Save.class}) Double getBalance() {
        return balance;
    }

    public void setBalance(@NotNull(groups = {ValidationGroup.Save.class}) Double balance) {
        this.balance = balance;
    }

    public @NotNull(groups = {ValidationGroup.Save.class}) Integer getUserId() {
        return userId;
    }

    public void setUserId(@NotNull(groups = {ValidationGroup.Save.class}) Integer userId) {
        this.userId = userId;
    }
}
