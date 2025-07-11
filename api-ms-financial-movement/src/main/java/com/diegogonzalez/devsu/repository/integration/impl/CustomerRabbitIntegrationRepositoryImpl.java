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
import org.springframework.dao.DataIntegrityViolationException;
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
        try {
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
        } catch (Exception e) {
            log.error("Error processing event {}: {}", event.getAction(), e.getMessage(), e);
        }
    }

    private void handleCustomerCreatedEvent(CustomerCreatedEvent event) {
        log.info("Customer creation event received: {}", event);
        processCustomerEvent(event, "creation");
    }

    private void handleCustomerModifiedEvent(CustomerModifiedEvent event) {
        log.info("Customer modification event received: {}", event);
        processCustomerEvent(event, "modification");
    }

    private void handleCustomerDeletedEvent(CustomerDeletedEvent event) {
        try {
            log.info("Customer deletion event received: {}", event);
            Customer customer = findCustomerByCustomerId(event.getCustomerId());

            List<Account> customerAccounts = accountRepository.findByCustomer(customer)
                    .stream()
                    .peek(account -> account.setStatus(Account.AccountStatus.INACTIVE))
                    .toList();

            accountRepository.saveAll(customerAccounts);
            log.info("Set {} accounts to inactive for customer: {}", customerAccounts.size(), customer.getCustomerId());
        } catch (MicroserviceException e) {
            log.error("Error processing customer deletion: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during customer deletion: {}", e.getMessage(), e);
        }
    }

    private void processCustomerEvent(AbstractEvent event, String eventType) {
        try {
            String customerId = getCustomerId(event);
            String identification = getIdentification(event);

            Customer customer = findOrCreateCustomer(customerId, identification);

            updateCustomerFromEvent(customer, event);

            customerRepository.save(customer);
            log.info("Customer {} event processed successfully for ID: {}", eventType, customerId);
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation during customer {} event: {}", eventType, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error processing customer {} event: {}", eventType, e.getMessage(), e);
        }
    }

    private Customer findOrCreateCustomer(String customerId, String identification) {
        Optional<Customer> customerByCustomerId = customerRepository.findCustomerByCustomerId(customerId);
        if (customerByCustomerId.isPresent()) {
            log.debug("Found customer by customerId: {}", customerId);
            return customerByCustomerId.get();
        }

        Optional<Customer> customerByIdentification = customerRepository.findCustomerByIdentification(identification);
        if (customerByIdentification.isPresent()) {
            log.info("Found customer with identification: {}, updating customerId to: {}", 
                    identification, customerId);
            Customer customer = customerByIdentification.get();
            customer.setCustomerId(customerId);
            return customer;
        }

        log.info("No existing customer found, creating new customer with ID: {}", customerId);
        return Customer.builder().customerId(customerId).build();
    }


    private void updateCustomerFromEvent(Customer customer, AbstractEvent event) {
        if (event instanceof CustomerCreatedEvent createdEvent) {
            updateCustomerProperties(customer, createdEvent);
        } else if (event instanceof CustomerModifiedEvent modifiedEvent) {
            updateCustomerProperties(customer, modifiedEvent);
        }
    }


    private void updateCustomerProperties(Customer customer, CustomerCreatedEvent event) {
        customer.setFirstName(event.getFirstName());
        customer.setLastName(event.getLastName());
        customer.setIdentification(event.getIdentification());
        customer.setGender(event.getGender());
        customer.setAge(event.getAge());
        customer.setPassword(event.getPassword());
        customer.setCustomerStatus(event.getCustomerStatus());
        customer.setAddress(event.getAddress());
        customer.setEmail(event.getEmail());
    }

    private void updateCustomerProperties(Customer customer, CustomerModifiedEvent event) {
        customer.setFirstName(event.getFirstName());
        customer.setLastName(event.getLastName());
        customer.setIdentification(event.getIdentification());
        customer.setGender(event.getGender());
        customer.setAge(event.getAge());
        customer.setPassword(event.getPassword());
        customer.setCustomerStatus(event.getCustomerStatus());
        customer.setAddress(event.getAddress());
        customer.setEmail(event.getEmail());
    }

    private Customer findCustomerByCustomerId(String customerId) {
        return customerRepository.findCustomerByCustomerId(customerId)
                .orElseThrow(() -> {
                    log.warn("Customer not found: {}", customerId);
                    return new MicroserviceException(ApplicationResponse.CUSTOMER_NOT_FOUND);
                });
    }

    private String getCustomerId(AbstractEvent event) {
        if (event instanceof CustomerCreatedEvent createdEvent) {
            return createdEvent.getCustomerId();
        } else if (event instanceof CustomerModifiedEvent modifiedEvent) {
            return modifiedEvent.getCustomerId();
        } else if (event instanceof CustomerDeletedEvent deletedEvent) {
            return deletedEvent.getCustomerId();
        }
        throw new IllegalArgumentException("Unsupported event type: " + event.getClass().getSimpleName());
    }

    private String getIdentification(AbstractEvent event) {
        if (event instanceof CustomerCreatedEvent createdEvent) {
            return createdEvent.getIdentification();
        } else if (event instanceof CustomerModifiedEvent modifiedEvent) {
            return modifiedEvent.getIdentification();
        }
        throw new IllegalArgumentException("Unsupported event type: " + event.getClass().getSimpleName());
    }
}
