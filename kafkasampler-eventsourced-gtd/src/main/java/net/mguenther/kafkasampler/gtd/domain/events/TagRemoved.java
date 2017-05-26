package net.mguenther.kafkasampler.gtd.domain.events;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Getter
@ToString
public class TagRemoved extends ItemEvent {

    private final String tag;

    public TagRemoved(final String id, final String tag) {
        this(UUID.randomUUID().toString().substring(0, 7), System.currentTimeMillis(), id, tag);
    }

    public TagRemoved(final String eventId, final long timestamp, final String id, final String tag) {
        super(eventId, timestamp, id);
        this.tag = tag;
    }
}
