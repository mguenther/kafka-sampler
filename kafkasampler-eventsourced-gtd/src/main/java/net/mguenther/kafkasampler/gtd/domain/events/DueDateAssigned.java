package net.mguenther.kafkasampler.gtd.domain.events;

import lombok.Getter;
import lombok.ToString;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Getter
@ToString
public class DueDateAssigned extends ItemEvent {

    private final long dueDate;

    public DueDateAssigned(final String id, final long dueDate) {
        this(UUID.randomUUID().toString().substring(0, 7), System.currentTimeMillis(), id, dueDate);
    }

    public DueDateAssigned(final String eventId, final long timestamp, final String id, final long dueDate) {
        super(eventId, timestamp, id);
        this.dueDate = dueDate;
    }
}
