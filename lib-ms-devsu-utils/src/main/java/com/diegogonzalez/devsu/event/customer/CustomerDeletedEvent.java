package com.diegogonzalez.devsu.event.customer;

import com.diegogonzalez.devsu.event.AbstractEvent;
import lombok.Getter;

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
public class CustomerDeletedEvent extends AbstractEvent {

    private static final String EVENT_TYPE = "CUSTOMER_DELETED";
    private final String customerId;
    private final String firstName;
    private final String lastName;

    public CustomerDeletedEvent(String customerId, String firstName, String lastName) {
        super(EVENT_TYPE);
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
