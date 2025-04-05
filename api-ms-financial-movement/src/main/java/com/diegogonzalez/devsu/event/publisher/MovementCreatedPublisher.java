package com.diegogonzalez.devsu.event.publisher;

import com.diegogonzalez.devsu.entity.Movement;
import com.diegogonzalez.devsu.event.movement.MovementCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

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
public class MovementCreatedPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishEvent(final Movement movement) {
        log.info("Publishing movement created event {}.", movement);
        MovementCreatedEvent movementCreatedEvent = new MovementCreatedEvent(this, movement);
        applicationEventPublisher.publishEvent(movementCreatedEvent);
    }

}
