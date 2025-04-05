package com.diegogonzalez.devsu.customer.configuration.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "rabbitmq.queue")
public class RabbitMQProperties {

    private String name;
    private String exchange;
    private Routing routing;

    @Data
    public static class Routing {
        private String key;
    }
}