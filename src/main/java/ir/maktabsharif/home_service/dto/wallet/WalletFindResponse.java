package ir.maktabsharif.home_service.dto.wallet;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.dto.user.UserFindResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class WalletFindResponse {
    private Integer id;
    private Double balance;
    private UserFindResponse user;
}
