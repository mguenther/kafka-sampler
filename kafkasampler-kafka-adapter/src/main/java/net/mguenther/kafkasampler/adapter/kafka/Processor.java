package net.mguenther.kafkasampler.adapter.kafka;

import java.util.concurrent.CompletableFuture;

/**
 * A {@code Processor} knows how to process application-level messages of type {@code MessageType} previously
 * committed to a Kafka log. Processing messages is done in an asynchronous fashion. Choosing the right
 * abstraction for asynchronous processing is a detail left to the implementors of this interface. This interface
 * only defines the contract in terms of the returned {@code CompletableFuture<Void>}.
 *
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public interface Processor<MessageType> {
    /**
     * Processes the given message asynchronously.
     *
     * @param message
     *      the message to process
     */
    CompletableFuture<Void> process(MessageType message);
}
