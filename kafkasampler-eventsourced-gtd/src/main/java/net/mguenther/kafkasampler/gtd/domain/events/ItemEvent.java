package net.mguenther.kafkasampler.gtd.domain.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@RequiredArgsConstructor
@Getter
abstract public class ItemEvent {

    private final String eventId;
    private final long timestamp;
    private final String itemId;
}
