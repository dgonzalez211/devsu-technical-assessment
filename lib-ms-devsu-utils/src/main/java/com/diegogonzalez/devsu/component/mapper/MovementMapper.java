package com.diegogonzalez.devsu.component.mapper;

import com.diegogonzalez.devsu.dto.MovementDTO;
import com.diegogonzalez.devsu.dto.request.MovementCreateRequestDTO;
import com.diegogonzalez.devsu.dto.request.MovementUpdateRequestDTO;
import com.diegogonzalez.devsu.dto.response.MovementResponseDTO;
import com.diegogonzalez.devsu.entity.Account;
import com.diegogonzalez.devsu.entity.Movement;
import com.diegogonzalez.devsu.entity.Movement.MovementStatus;
import com.diegogonzalez.devsu.entity.Movement.MovementType;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/*
 * Author: Diego González
 *
 * This code is the exclusive property of the author. Any unauthorized use,
 * distribution, or modification is prohibited without the author's explicit consent.
 *
 * Disclaimer: This code is provided "as is" without any warranties of any kind,
 * either express or implied, including but not limited to warranties of merchantability
 * or fitness for a particular purpose.
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MovementMapper {

    MovementMapper INSTANCE = Mappers.getMapper(MovementMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "account", source = "accountId", qualifiedByName = "accountIdToAccount")
    @Mapping(target = "type", source = "type", qualifiedByName = "stringToMovementType")
    @Mapping(target = "status", constant = "COMPLETED")
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Movement toEntity(MovementCreateRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "account", source = "accountId", qualifiedByName = "accountIdToAccount")
    @Mapping(target = "type", source = "type", qualifiedByName = "stringToMovementType")
    @Mapping(target = "status", source = "status", qualifiedByName = "stringToMovementStatus")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Movement toEntity(MovementDTO dto);

    @Mapping(target = "accountId", source = "account.uuid")
    @Mapping(target = "type", source = "type", qualifiedByName = "movementTypeToString")
    @Mapping(target = "status", source = "status", qualifiedByName = "movementStatusToString")
    MovementDTO toDto(Movement entity);

    @Mapping(target = "accountId", source = "account.uuid")
    @Mapping(target = "accountNumber", source = "account.accountNumber")
    @Mapping(target = "customerFirstName", source = "account.customer.firstName")
    @Mapping(target = "customerLastName", source = "account.customer.lastName")
    @Mapping(target = "type", source = "type", qualifiedByName = "movementTypeToString")
    @Mapping(target = "status", source = "status", qualifiedByName = "movementStatusToString")
    MovementResponseDTO toResponseDto(Movement entity);

    List<MovementDTO> toDtoList(List<Movement> entities);

    List<MovementResponseDTO> toResponseDtoList(List<Movement> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "referenceNumber", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "type", source = "type", qualifiedByName = "stringToMovementType")
    @Mapping(target = "status", source = "status", qualifiedByName = "stringToMovementStatus")
    void updateEntityFromDto(MovementUpdateRequestDTO dto, @MappingTarget Movement entity);


    @Named("accountIdToAccount")
    default Account accountIdToAccount(UUID accountId) {
        if (accountId == null) {
            return null;
        }
        Account account = new Account();
        account.setUuid(accountId);
        return account;
    }


    @Named("stringToMovementType")
    default MovementType stringToMovementType(String type) {
        if (type == null) {
            return null;
        }
        try {
            return MovementType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }


    @Named("movementTypeToString")
    default String movementTypeToString(MovementType type) {
        return type != null ? type.name() : null;
    }


    @Named("stringToMovementStatus")
    default MovementStatus stringToMovementStatus(String status) {
        if (status == null) {
            return null;
        }
        try {
            return MovementStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }


    @Named("movementStatusToString")
    default String movementStatusToString(MovementStatus status) {
        return status != null ? status.name() : null;
    }


    default BigDecimal calculateBalance(Account account, BigDecimal amount, MovementType type) {
        if (account == null || account.getCurrentBalance() == null || amount == null || type == null) {
            return null;
        }

        BigDecimal currentBalance = account.getCurrentBalance();

        switch (type) {
            case DEPOSIT:
            case TRANSFER_IN:
            case INTEREST:
                return currentBalance.add(amount);
            case WITHDRAWAL:
            case TRANSFER_OUT:
            case FEE:
                return currentBalance.subtract(amount);
            case ADJUSTMENT:
            case PAYMENT:

                return amount.compareTo(BigDecimal.ZERO) >= 0
                        ? currentBalance.add(amount)
                        : currentBalance.subtract(amount.abs());
            default:
                return currentBalance;
        }
    }
}