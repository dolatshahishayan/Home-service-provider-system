package ir.maktabsharif.home_service.mapper.transaction;

import ir.maktabsharif.home_service.dto.transaction.TransactionFindResponse;
import ir.maktabsharif.home_service.model.transaction.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TransactionMapper {
    @Mapping(source = "sender.id", target = "senderId")
    @Mapping(source = "receiver.id", target = "receiverId")
    TransactionFindResponse mapToResponse(Transaction transaction);
}