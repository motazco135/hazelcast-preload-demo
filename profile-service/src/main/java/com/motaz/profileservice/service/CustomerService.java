package com.motaz.profileservice.service;

import com.motaz.profileservice.dto.CreateCustomerRequest;
import com.motaz.profileservice.dto.CustomerProfileDto;
import com.motaz.profileservice.dto.UpdateCustomerRequest;
import com.motaz.profileservice.mapper.CustomerMapper;
import com.motaz.profileservice.model.CustomerProfileEntity;
import com.motaz.profileservice.repo.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final CustomerEventProducer customerEventProducer;

    @Transactional
    public CustomerProfileDto createCustomer(CreateCustomerRequest request) {
        CustomerProfileEntity entity = customerMapper.toEntity(request);
        final CustomerProfileEntity savedEntity = customerRepository.save(entity);
        CustomerProfileDto response = customerMapper.toResponse(savedEntity);

        // Publish to Kafka
        customerEventProducer.publishCustomerProfile(response);

        return response;
    }

    public Optional<CustomerProfileDto> getCustomerByCustomerId(Long customerId) {
        return customerRepository.findById(customerId)
                .map(customerMapper::toResponse);
    }

    @Transactional
    public Optional<CustomerProfileDto> updateCustomer(Long customerId, UpdateCustomerRequest request) {
        return customerRepository.findById(customerId)
                .map(entity -> {
                    customerMapper.updateEntityFromRequest(request, entity);
                    CustomerProfileEntity updatedEntity = customerRepository.save(entity);
                    CustomerProfileDto response = customerMapper.toResponse(updatedEntity);

                    // Publish to Kafka
                    //customerEventProducer.publishCustomerProfile(response);

                    return response;
                });
    }

    @Transactional(readOnly = true)
    public Page<CustomerProfileDto> getAllCustomers(Pageable pageable) {
        Page<CustomerProfileEntity> entities = customerRepository.findAll(pageable);
        return entities.map(customerMapper::toResponse);
    }

}
