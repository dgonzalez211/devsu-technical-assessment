package com.diegogonzalez.devsu.customer.service;

import com.diegogonzalez.devsu.dto.CustomerDTO;
import com.diegogonzalez.devsu.dto.request.CustomerCreateRequestDTO;
import com.diegogonzalez.devsu.dto.request.CustomerUpdateRequestDTO;

import java.util.List;

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


    List<CustomerDTO> findCustomers();


    CustomerDTO findCustomerById(String customerId);


    CustomerDTO createCustomer(CustomerCreateRequestDTO customerRequestDTO);


    CustomerDTO updateCustomer(String customerId, CustomerUpdateRequestDTO customerRequestDTO);


    void removeCustomer(String customerId);
}
