package ir.maktabsharif.home_service.mapper.wallet;

import ir.maktabsharif.home_service.dto.wallet.WalletFindResponse;
import ir.maktabsharif.home_service.dto.wallet.WalletSaveUpdateRequest;
import ir.maktabsharif.home_service.model.wallet.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WalletMapper {
    Wallet mapToEntity(WalletSaveUpdateRequest walletSaveUpdateRequest);
    @Mapping(source = "user.id", target = "userId")
    WalletFindResponse mapToResponse(Wallet wallet);
}
