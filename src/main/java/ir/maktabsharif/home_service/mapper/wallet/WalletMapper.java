package ir.maktabsharif.home_service.mapper.wallet;

import ir.maktabsharif.home_service.dto.wallet.WalletFindResponse;
import ir.maktabsharif.home_service.dto.wallet.WalletSaveUpdateRequest;
import ir.maktabsharif.home_service.model.wallet.Wallet;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WalletMapper {
    Wallet mapToEntity(WalletSaveUpdateRequest walletSaveUpdateRequest);
    void updateEntityWithDTO(WalletSaveUpdateRequest walletSaveUpdateRequest, @MappingTarget Wallet wallet);
    @Mapping(source = "user.id", target = "userId")
    WalletFindResponse mapToResponse(Wallet wallet);
}
