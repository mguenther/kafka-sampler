package net.mguenther.kafkasampler.gtd.persistence;

import lombok.RequiredArgsConstructor;
import net.mguenther.kafkasampler.adapter.kafka.Processor;
import net.mguenther.kafkasampler.gtd.EventReceiver;
import net.mguenther.kafkasampler.gtd.domain.events.ItemEvent;

import java.util.concurrent.CompletableFuture;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@RequiredArgsConstructor
public class ItemEventProcessor implements Processor<ItemEvent> {

    private final EventReceiver eventReceiver;

    @Override
    public CompletableFuture<Void> process(final ItemEvent event) {

        return eventReceiver.onEvent(event);
    }
}
