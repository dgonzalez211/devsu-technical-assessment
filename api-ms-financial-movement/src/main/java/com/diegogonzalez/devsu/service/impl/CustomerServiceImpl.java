package com.diegogonzalez.devsu.service.impl;

import com.diegogonzalez.devsu.dto.CustomerDTO;
import com.diegogonzalez.devsu.entity.Customer;
import com.diegogonzalez.devsu.exception.ApplicationResponse;
import com.diegogonzalez.devsu.exception.MicroserviceException;
import com.diegogonzalez.devsu.repository.CustomerRepository;
import com.diegogonzalez.devsu.restclient.CustomerRestClient;
import com.diegogonzalez.devsu.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
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
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRestClient restClient;
    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    @Override
    public Customer findCustomer(UUID customerId) {
        return customerRepository.findCustomerByUuid(customerId)
                .or(() -> Optional.ofNullable(retrieveExternalCustomer(customerId)))
                .orElseThrow(() -> new MicroserviceException(ApplicationResponse.CUSTOMER_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public Customer retrieveExternalCustomer(UUID customerId) {
        CustomerDTO externalCustomer = restClient.retrieveExternalCustomer(customerId.toString());
        Customer customer = Customer.builder()
                .customerId(UUID.fromString(externalCustomer.getCustomerId()))
                .firstName(externalCustomer.getFirstName())
                .lastName(externalCustomer.getLastName())
                .build();
        customerRepository.save(customer);
        return customer;
    }
}
