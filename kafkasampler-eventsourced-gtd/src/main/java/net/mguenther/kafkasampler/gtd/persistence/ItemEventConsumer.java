package net.mguenther.kafkasampler.gtd.persistence;

import net.mguenther.kafkasampler.adapter.kafka.Consumer;
import net.mguenther.kafkasampler.adapter.kafka.ConsumerSettings;
import net.mguenther.kafkasampler.gtd.domain.events.ItemEvent;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public class ItemEventConsumer extends Consumer<byte[], ItemEvent> {

    public ItemEventConsumer(final String consumerId,
                             final String topic,
                             final ConsumerSettings<byte[], ItemEvent> consumerSettings) {
        super(consumerId, topic, consumerSettings);
    }
}
