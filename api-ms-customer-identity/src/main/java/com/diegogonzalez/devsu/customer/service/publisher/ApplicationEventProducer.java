package com.diegogonzalez.devsu.customer.service.publisher;

import com.diegogonzalez.devsu.customer.configuration.properties.RabbitMQProperties;
import com.diegogonzalez.devsu.event.AbstractEvent;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

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
@Service
@RequiredArgsConstructor
public class ApplicationEventProducer {
    private final RabbitMQProperties rabbitProperties;
    private final RabbitTemplate rabbitTemplate;

    public void publish(AbstractEvent event) {
        try {
            log.info("Publishing event: {}", event);
            this.rabbitTemplate.convertAndSend(rabbitProperties.getExchange(), rabbitProperties.getRouting().getKey(), event);
        } catch (AmqpException amqpException) {
            log.error("Unexpected exception when publishing event {}, caused by {}", event, amqpException.getMessage());
        }
    }
}
