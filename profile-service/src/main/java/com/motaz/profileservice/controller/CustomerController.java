package com.motaz.profileservice.controller;

import com.motaz.profileservice.dto.CreateCustomerRequest;
import com.motaz.profileservice.dto.CustomerProfileDto;
import com.motaz.profileservice.dto.UpdateCustomerRequest;
import com.motaz.profileservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final PagedResourcesAssembler<CustomerProfileDto> pagedAssembler;


    @PostMapping
    public ResponseEntity<CustomerProfileDto> createCustomer(@RequestBody CreateCustomerRequest request) {
        CustomerProfileDto response = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerProfileDto> getCustomerById(@PathVariable Long customerId) {
        return customerService.getCustomerByCustomerId(customerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerProfileDto> updateCustomer(@PathVariable Long customerId,
                                                             @RequestBody UpdateCustomerRequest request) {
        return customerService.updateCustomer(customerId, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public PagedModel<EntityModel<CustomerProfileDto>> getAllCustomers(
            @ParameterObject
            @PageableDefault(size = 20, sort = "customerId") Pageable pageable,
            PagedResourcesAssembler<CustomerProfileDto> assembler ) {
        Page<CustomerProfileDto> customers = customerService.getAllCustomers(pageable);
        return  assembler.toModel(customers);
    }

}
