package com.diegogonzalez.devsu.customer.service.impl;


import com.diegogonzalez.devsu.component.mapper.CustomerMapper;
import com.diegogonzalez.devsu.customer.repository.CustomerRepository;
import com.diegogonzalez.devsu.customer.service.CustomerService;
import com.diegogonzalez.devsu.customer.service.publisher.ApplicationEventProducer;
import com.diegogonzalez.devsu.dto.CustomerDTO;
import com.diegogonzalez.devsu.dto.request.CustomerCreateRequestDTO;
import com.diegogonzalez.devsu.dto.request.CustomerUpdateRequestDTO;
import com.diegogonzalez.devsu.entity.Customer;
import com.diegogonzalez.devsu.entity.Person;
import com.diegogonzalez.devsu.event.customer.CustomerCreatedEvent;
import com.diegogonzalez.devsu.event.customer.CustomerDeletedEvent;
import com.diegogonzalez.devsu.event.customer.CustomerModifiedEvent;
import com.diegogonzalez.devsu.exception.ApplicationResponse;
import com.diegogonzalez.devsu.exception.MicroserviceException;
import com.diegogonzalez.devsu.utils.FieldValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
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
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ApplicationEventProducer applicationEventProducer;

    @Override
    public List<CustomerDTO> findCustomers() {
        log.debug("Fetching all customers");
        List<CustomerDTO> customers = customerRepository.findAll(Sort.by(Sort.Direction.ASC, "firstName"))
                .stream()
                .map(CustomerMapper.INSTANCE::toDto)
                .toList();

        if (customers.isEmpty()) {
            throw new MicroserviceException(ApplicationResponse.CUSTOMER_NOT_FOUND, HttpStatus.NOT_FOUND.value());
        }

        return customers;
    }

    @Override
    public CustomerDTO findCustomerById(String customerId) {
        log.debug("Finding customer with ID: {}", customerId);
        return CustomerMapper.INSTANCE.toDto(findCustomerEntityById(customerId));
    }

    @Override
    @Transactional
    public CustomerDTO createCustomer(CustomerCreateRequestDTO request) {
        log.debug("Creating new customer: {}", request);

        FieldValidator<CustomerCreateRequestDTO> validator = FieldValidator.create();

        Set<String> errors = validator.apply(request);

        if (!errors.isEmpty()) {
            throw new MicroserviceException(ApplicationResponse.CUSTOMER_VALIDATION_ERROR, HttpStatus.BAD_REQUEST.value());
        }

        Customer customer = CustomerMapper.INSTANCE.toEntity(request);
        Customer savedCustomer = customerRepository.save(customer);

        applicationEventProducer.publish(
                new CustomerCreatedEvent(
                        savedCustomer.getCustomerId(),
                        savedCustomer.getFirstName(),
                        savedCustomer.getLastName(),
                        savedCustomer.getIdentification(),
                        savedCustomer.getGender(),
                        savedCustomer.getAge(),
                        savedCustomer.getPassword(),
                        savedCustomer.getCustomerStatus(),
                        savedCustomer.getAddress(),
                        savedCustomer.getEmail()
                )
        );

        return CustomerMapper.INSTANCE.toDto(savedCustomer);
    }

    @Override
    @Transactional
    public CustomerDTO updateCustomer(String customerId, CustomerUpdateRequestDTO request) {
        log.debug("Updating customer with ID: {}, new data: {}", customerId, request);
        Customer customer = findCustomerEntityById(customerId);
        updateCustomerFields(customer, request);
        Customer updatedCustomer = customerRepository.save(customer);

        applicationEventProducer.publish(
                new CustomerModifiedEvent(
                        updatedCustomer.getCustomerId(),
                        updatedCustomer.getFirstName(),
                        updatedCustomer.getLastName(),
                        updatedCustomer.getIdentification(),
                        updatedCustomer.getGender(),
                        updatedCustomer.getAge(),
                        updatedCustomer.getPassword(),
                        updatedCustomer.getCustomerStatus(),
                        updatedCustomer.getAddress(),
                        updatedCustomer.getEmail()
                )
        );

        return CustomerMapper.INSTANCE.toDto(updatedCustomer);
    }

    @Override
    @Transactional
    public void removeCustomer(String customerId) {
        log.debug("Soft deleting customer with ID: {}", customerId);
        Customer customer = findCustomerEntityById(customerId);
        customer.setStatus(Person.PersonStatus.INACTIVE);
        customerRepository.save(customer);

        applicationEventProducer.publish(
                new CustomerDeletedEvent(
                        customer.getCustomerId(),
                        customer.getFirstName(),
                        customer.getLastName()
                )
        );
    }

    private Customer findCustomerEntityById(String customerId) {
        try {
            return customerRepository.findByCustomerId(customerId)
                    .orElseThrow(() -> new MicroserviceException(
                            ApplicationResponse.CUSTOMER_NOT_FOUND,
                            HttpStatus.NOT_FOUND.value()));
        } catch (IllegalArgumentException e) {
            // If not a valid UUID, try to find by customerId
            return customerRepository.findByUuid(UUID.fromString(customerId))
                    .orElseThrow(() -> new MicroserviceException(
                            ApplicationResponse.CUSTOMER_NOT_FOUND,
                            HttpStatus.NOT_FOUND.value()));
        }
    }

    private void updateCustomerFields(Customer customer, CustomerUpdateRequestDTO request) {
        customer.setFirstName(Objects.requireNonNullElse(request.getFirstName(), customer.getFirstName()));
        customer.setLastName(Objects.requireNonNullElse(request.getLastName(), customer.getLastName()));
        customer.setAddress(Objects.requireNonNullElse(request.getAddress(), customer.getAddress()));
    }
}
