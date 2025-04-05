package com.diegogonzalez.devsu.event.movement;

import com.diegogonzalez.devsu.entity.Movement;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

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
public class MovementCreatedEvent extends ApplicationEvent {
    private final Movement movement;

    public MovementCreatedEvent(Object source, Movement movement) {
        super(source);
        this.movement = movement;
    }
}
