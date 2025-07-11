package com.diegogonzalez.devsu.restclient;

import com.diegogonzalez.devsu.dto.CustomerDTO;
import com.diegogonzalez.devsu.dto.response.IntegrationResponse;
import com.diegogonzalez.devsu.exception.ApplicationResponse;
import com.diegogonzalez.devsu.exception.MicroserviceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

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
@Component
@RequiredArgsConstructor
public class CustomerRestClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    @Value("${rest.client.service.customer-identity.endpoint}")
    private String customerIdentityEndpoint;

    public CustomerDTO retrieveExternalCustomer(String customerId) {
        log.info("Retrieving external customer with id {}", customerId);

        try {
            IntegrationResponse response = restClient.get()
                    .uri(customerIdentityEndpoint.concat("/{customerId}"), customerId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(IntegrationResponse.class);

            if (response == null) {
                throw new MicroserviceException(ApplicationResponse.REST_CLIENT);
            }

            if (!response.getCode().equals(ApplicationResponse.SUCCESS.getCode())) {
                log.error("Unexpected response from service {}", response);
                throw new MicroserviceException(ApplicationResponse.REST_CLIENT);
            }

            if (response.getData() == null) {
                log.error("No data found for customer {}", customerId);
                throw new MicroserviceException(ApplicationResponse.REST_CLIENT);
            }

            try {
                // Convert the LinkedHashMap to CustomerDTO using ObjectMapper
                return objectMapper.convertValue(response.getData(), CustomerDTO.class);
            } catch (Exception e) {
                log.error("Error converting response data to CustomerDTO for customer {}: {}", customerId, e.getMessage());
                throw new MicroserviceException(ApplicationResponse.REST_CLIENT, e);
            }

        } catch (RestClientException exc) {
            throw new MicroserviceException(ApplicationResponse.REST_CLIENT, exc);
        }
    }
}
