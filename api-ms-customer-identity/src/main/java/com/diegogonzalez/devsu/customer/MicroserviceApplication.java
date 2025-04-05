package com.diegogonzalez.devsu.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

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
@EnableJpaRepositories(basePackages = {"com.diegogonzalez.devsu.customer.repository", "com.diegogonzalez.devsu.entity"})
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.diegogonzalez.devsu.customer", "com.diegogonzalez.devsu.entity"})
@EntityScan(basePackages = {"com.diegogonzalez.devsu.entity"})
@ConfigurationPropertiesScan
@EnableConfigurationProperties
public class MicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceApplication.class, args);
    }

}
