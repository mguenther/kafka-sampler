package net.mguenther.kafkasampler.gtd;

import net.mguenther.kafkasampler.gtd.domain.events.ItemEvent;

import java.util.concurrent.CompletableFuture;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public interface EventReceiver {

    CompletableFuture<Void> onEvent(ItemEvent event);
}
