package net.mguenther.kafkasampler.gtd.persistence;

import net.mguenther.kafkasampler.adapter.kafka.Producer;
import net.mguenther.kafkasampler.adapter.kafka.ProducerSettings;
import net.mguenther.kafkasampler.gtd.domain.events.ItemEvent;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public class ItemEventProducer extends Producer<ItemEvent, byte[]> {

    private final String itemTopic;

    public ItemEventProducer(final String producerId,
                             final String itemTopic,
                             final ProducerSettings<ItemEvent, byte[]> producerSettings) {
        super(producerId, producerSettings);
        this.itemTopic = itemTopic;
    }

    public void log(final ItemEvent message) {
        log(itemTopic, message.getItemId(), message);
    }
}
