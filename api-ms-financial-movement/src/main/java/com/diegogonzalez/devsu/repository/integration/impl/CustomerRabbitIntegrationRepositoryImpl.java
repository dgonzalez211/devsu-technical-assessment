package com.diegogonzalez.devsu.repository.integration.impl;

import com.diegogonzalez.devsu.entity.Account;
import com.diegogonzalez.devsu.entity.Customer;
import com.diegogonzalez.devsu.event.AbstractEvent;
import com.diegogonzalez.devsu.event.customer.CustomerCreatedEvent;
import com.diegogonzalez.devsu.event.customer.CustomerDeletedEvent;
import com.diegogonzalez.devsu.event.customer.CustomerModifiedEvent;
import com.diegogonzalez.devsu.exception.ApplicationResponse;
import com.diegogonzalez.devsu.exception.MicroserviceException;
import com.diegogonzalez.devsu.repository.AccountRepository;
import com.diegogonzalez.devsu.repository.CustomerRepository;
import com.diegogonzalez.devsu.repository.integration.CustomerIntegrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
@Repository
@RequiredArgsConstructor
public class CustomerRabbitIntegrationRepositoryImpl implements CustomerIntegrationRepository {
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    @RabbitListener(queues = "customers.queue")
    public void handleEvent(AbstractEvent event) {
        switch (event.getAction()) {
            case "CUSTOMER_CREATED":
                handleCustomerCreatedEvent((CustomerCreatedEvent) event);
                break;
            case "CUSTOMER_MODIFIED":
                handleCustomerModifiedEvent((CustomerModifiedEvent) event);
                break;
            case "CUSTOMER_DELETED":
                handleCustomerDeletedEvent((CustomerDeletedEvent) event);
                break;
            default:
                log.warn("Unhandled event type: {}", event.getAction());
        }
    }

    private void handleCustomerCreatedEvent(CustomerCreatedEvent event) {
        try {
            log.info("Customer creation event received, creating customer: {}", event);
            customerRepository.save(Customer.builder()
                    .customerId(event.getCustomerId())
                    .firstName(event.getFirstName())
                    .lastName(event.getLastName())
                    .build());
            log.info("Customer creation event processed successfully");
        } catch (Exception exc) {
            log.info("Unhandled exception when processing customer creation: {0}.", exc);
        }
    }

    private void handleCustomerModifiedEvent(CustomerModifiedEvent event) {
        try {
            log.info("Customer modification event received, modifying customer: {}", event);
            customerRepository.save(Customer.builder()
                    .customerId(event.getCustomerId())
                    .firstName(event.getFirstName())
                    .lastName(event.getLastName())
                    .build());
            log.info("Customer modification event processed successfully");
        } catch (Exception exc) {
            log.info("Unhandled exception when processing customer modification event: {0}.", exc);
        }
    }

    private void handleCustomerDeletedEvent(CustomerDeletedEvent event) {
        try {
            log.info("Customer deletion event received, removing customer {} ", event);
            log.info("Customer accounts will be set to inactive");
            Optional<Customer> cliente = customerRepository.findCustomerByUuid((event.getCustomerId()));

            if (cliente.isEmpty()) {
                log.info("Customer not found: {}", event.getCustomerId());
                throw new MicroserviceException(ApplicationResponse.CUSTOMER_NOT_FOUND);
            }

            List<Account> cuentasCliente = accountRepository.findByCustomer(cliente.get())
                    .stream().peek(cuenta -> cuenta.setStatus(Account.AccountStatus.INACTIVE)).toList();
            accountRepository.saveAll(cuentasCliente);

            log.info("Customer deletion event processed successfully");
            log.info("Customer accounts set to inactive");
        } catch (Exception exc) {
            log.info("Unhandled exception when processing customer deletion event {0}.", exc);
        }
    }
}
