package com.diegogonzalez.devsu.event;

import lombok.Getter;

import java.time.Instant;
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
public abstract class AbstractEvent {

    private final UUID eventId;
    private final String action;
    private final Instant createdAt;

    protected AbstractEvent(String action) {
        eventId = UUID.randomUUID();
        createdAt = Instant.now();
        this.action = action;
    }
}