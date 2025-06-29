package ir.maktabsharif.home_service.dto.wallet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WalletFindResponse {
    private Integer id;
    private Double balance;
    private Integer userId;
}
