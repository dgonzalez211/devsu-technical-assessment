package com.diegogonzalez.devsu.component.mapper;

import com.diegogonzalez.devsu.dto.CustomerDTO;
import com.diegogonzalez.devsu.dto.request.CustomerCreateRequestDTO;
import com.diegogonzalez.devsu.dto.request.CustomerUpdateRequestDTO;
import com.diegogonzalez.devsu.dto.response.CustomerResponseDTO;
import com.diegogonzalez.devsu.entity.Customer;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

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
public interface CustomerMapper {

    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "status", source = "customerStatus", defaultValue = "ACTIVE")
    @Mapping(target = "accounts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "passwordResetToken", ignore = true)
    @Mapping(target = "passwordResetExpiry", ignore = true)
    @Mapping(target = "failedLoginAttempts", ignore = true)
    @Mapping(target = "lockedUntil", ignore = true)
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "identification", source = "identification")
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "age", source = "age")
    @Mapping(target = "customerStatus", source = "customerStatus")
    Customer toEntity(CustomerCreateRequestDTO dto);

    CustomerDTO toDto(Customer entity);

    @Mapping(target = "accountCount", expression = "java(entity.getAccounts() != null ? entity.getAccounts().size() : 0)")
    CustomerResponseDTO toResponseDto(Customer entity);

    List<CustomerDTO> toDtoList(List<Customer> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "passwordResetToken", ignore = true)
    @Mapping(target = "passwordResetExpiry", ignore = true)
    @Mapping(target = "failedLoginAttempts", ignore = true)
    @Mapping(target = "lockedUntil", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    void updateEntityFromDto(CustomerUpdateRequestDTO dto, @MappingTarget Customer entity);


    @Named("updatePassword")
    default void updatePassword(Customer customer, String newPassword) {
        if (newPassword != null && !newPassword.isEmpty()) {
            customer.setPassword(newPassword);


        }
    }


    default CustomerResponseDTO toResponseDtoWithAccounts(Customer entity) {
        CustomerResponseDTO dto = toResponseDto(entity);
        if (entity.getAccounts() != null) {
            dto.setAccountCount(entity.getAccounts().size());
        }
        return dto;
    }
}
