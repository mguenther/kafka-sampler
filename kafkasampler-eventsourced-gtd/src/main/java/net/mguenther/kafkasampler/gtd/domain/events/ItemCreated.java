package net.mguenther.kafkasampler.gtd.domain.events;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Getter
@ToString
public class ItemCreated extends ItemEvent {

    private final String description;

    public ItemCreated(final String id, final String description) {
        this(UUID.randomUUID().toString().substring(0, 7), System.currentTimeMillis(), id, description);
    }

    public ItemCreated(final String eventId, final long timestamp, final String id, final String description) {
        super(eventId, timestamp, id);
        this.description = description;
    }
}
