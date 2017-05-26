package net.mguenther.kafkasampler.gtd.domain.events;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Getter
@ToString
public class RequiredTimeAssigned extends ItemEvent {

    private final int requiredTime;

    public RequiredTimeAssigned(final String id, final int requiredTime) {
        this(UUID.randomUUID().toString().substring(0, 7), System.currentTimeMillis(), id, requiredTime);
    }

    public RequiredTimeAssigned(final String eventId, final long timestamp, final String id, final int requiredTime) {
        super(eventId, timestamp, id);
        this.requiredTime = requiredTime;
    }
}
