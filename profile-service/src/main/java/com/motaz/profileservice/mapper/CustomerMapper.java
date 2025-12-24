package com.motaz.profileservice.mapper;

import com.motaz.profileservice.dto.CreateCustomerRequest;
import com.motaz.profileservice.dto.CustomerProfileDto;
import com.motaz.profileservice.dto.UpdateCustomerRequest;
import com.motaz.profileservice.model.CustomerProfileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    // Request to Entity
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CustomerProfileEntity toEntity(CreateCustomerRequest request);

    // Update Entity from Request
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(UpdateCustomerRequest request, @MappingTarget CustomerProfileEntity entity);

    // Entity to Response
    CustomerProfileDto toResponse(CustomerProfileEntity entity);
}
