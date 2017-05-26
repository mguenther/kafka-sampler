package net.mguenther.kafkasampler.gtd.domain.events;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Getter
@ToString
public class ItemMovedToList extends ItemEvent {

    private final String list;

    public ItemMovedToList(final String id, final String list) {
        this(UUID.randomUUID().toString().substring(0, 7), System.currentTimeMillis(), id, list);
    }

    public ItemMovedToList(final String eventId, final long timestamp, final String id, final String list) {
        super(eventId, timestamp, id);
        this.list = list;
    }
}
