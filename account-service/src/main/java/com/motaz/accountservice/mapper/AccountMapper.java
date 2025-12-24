package com.motaz.accountservice.mapper;

import com.motaz.accountservice.dto.AccountDto;
import com.motaz.accountservice.dto.CreateAccountRequest;
import com.motaz.accountservice.dto.UpdateAccountRequest;
import com.motaz.accountservice.model.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    // Request to Entity
    @Mapping(target = "openedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AccountEntity toEntity(CreateAccountRequest request);


    // Update Entity from Request
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "iban", ignore = true)
    @Mapping(target = "openedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(UpdateAccountRequest request, @MappingTarget AccountEntity entity);

    // Entity to Response
    AccountDto toResponse(AccountEntity entity);
}
