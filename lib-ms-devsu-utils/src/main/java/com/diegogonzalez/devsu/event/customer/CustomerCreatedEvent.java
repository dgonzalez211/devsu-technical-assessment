package com.diegogonzalez.devsu.event.customer;

import com.diegogonzalez.devsu.entity.Customer;
import com.diegogonzalez.devsu.entity.Person;
import com.diegogonzalez.devsu.event.AbstractEvent;
import lombok.Getter;

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
@Getter
public class CustomerCreatedEvent extends AbstractEvent {

    private static final String EVENT_TYPE = "CUSTOMER_CREATED";
    private final String customerId;
    private final String firstName;
    private final String lastName;
    private final String identification;
    private final Person.Gender gender;
    private final Integer age;
    private final String password;
    private final Customer.CustomerStatus customerStatus;
    private final String address;
    private final String email;

    public CustomerCreatedEvent(
            String customerId, 
            String firstName, 
            String lastName, 
            String identification, 
            Person.Gender gender, 
            Integer age, 
            String password, 
            Customer.CustomerStatus customerStatus,
            String address,
            String email) {
        super(EVENT_TYPE);
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.identification = identification;
        this.gender = gender;
        this.age = age;
        this.password = password;
        this.customerStatus = customerStatus;
        this.address = address;
        this.email = email;
    }
}
