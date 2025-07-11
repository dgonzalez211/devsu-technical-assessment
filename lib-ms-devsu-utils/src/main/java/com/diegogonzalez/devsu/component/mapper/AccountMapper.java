package com.diegogonzalez.devsu.component.mapper;

import com.diegogonzalez.devsu.dto.AccountDTO;
import com.diegogonzalez.devsu.dto.request.AccountCreateRequestDTO;
import com.diegogonzalez.devsu.dto.request.AccountUpdateRequestDTO;
import com.diegogonzalez.devsu.dto.response.AccountResponseDTO;
import com.diegogonzalez.devsu.entity.Account;
import com.diegogonzalez.devsu.entity.Account.AccountStatus;
import com.diegogonzalez.devsu.entity.Customer;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
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
public interface AccountMapper {

    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", source = "customerId", qualifiedByName = "customerIdToCustomer")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "currentBalance", source = "initialBalance")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "openedAt", ignore = true)
    @Mapping(target = "lastTransactionAt", ignore = true)
    @Mapping(target = "closedAt", ignore = true)
    Account toEntity(AccountCreateRequestDTO dto);

    @Mapping(target = "customerId", source = "customer.customerId")
    AccountDTO toDto(Account entity);

    @Mapping(target = "customerId", source = "customer.customerId")
    @Mapping(target = "customerFirstName", source = "customer.firstName")
    @Mapping(target = "customerLastName", source = "customer.lastName")
    AccountResponseDTO toResponseDto(Account entity);

    List<AccountDTO> toDtoList(List<Account> entities);

    List<AccountResponseDTO> toResponseDtoList(List<Account> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "initialBalance", ignore = true)
    @Mapping(target = "currentBalance", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "openedAt", ignore = true)
    @Mapping(target = "lastTransactionAt", ignore = true)
    @Mapping(target = "closedAt", expression = "java(updateClosedAtBasedOnStatus(entity, dto.getStatus()))")
    void updateEntityFromDto(AccountUpdateRequestDTO dto, @MappingTarget Account entity);


    @Named("customerIdToCustomer")
    default Customer customerIdToCustomer(String customerId) {
        if (customerId == null) {
            return null;
        }
        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        return customer;
    }


    default java.time.LocalDateTime updateClosedAtBasedOnStatus(Account entity, AccountStatus newStatus) {
        if (newStatus == AccountStatus.CLOSED && entity.getStatus() != AccountStatus.CLOSED) {
            return LocalDateTime.now();
        } else if (newStatus != null && newStatus != AccountStatus.CLOSED && entity.getStatus() == AccountStatus.CLOSED) {
            return null;
        }
        return entity.getClosedAt();
    }
}
