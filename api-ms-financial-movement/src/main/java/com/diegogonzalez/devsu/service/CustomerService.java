package com.diegogonzalez.devsu.service;

import com.diegogonzalez.devsu.entity.Customer;
import org.springframework.transaction.annotation.Transactional;

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
public interface CustomerService {

    @Transactional(readOnly = true)
    Customer findCustomer(UUID customerId);

    Customer retrieveExternalCustomer(UUID customerId);

}
