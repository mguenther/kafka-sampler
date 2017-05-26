package net.mguenther.kafkasampler.gtd.domain.events;

import lombok.ToString;

import java.util.UUID;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@ToString
public class ItemConcluded extends ItemEvent {

    public ItemConcluded(final String id) {
        this(UUID.randomUUID().toString().substring(0, 7), System.currentTimeMillis(), id);
    }

    public ItemConcluded(final String eventId, final long timestamp, final String id) {
        super(eventId, timestamp, id);
    }
}
