package net.mguenther.kafkasampler.gtd;

import net.mguenther.kafkasampler.adapter.kafka.ConsumerSettings;
import net.mguenther.kafkasampler.adapter.kafka.ProducerSettings;
import net.mguenther.kafkasampler.gtd.domain.events.ItemEvent;
import net.mguenther.kafkasampler.gtd.persistence.AvroDomainConverter;
import net.mguenther.kafkasampler.gtd.persistence.ItemEventCodec;
import net.mguenther.kafkasampler.gtd.persistence.ItemEventConsumer;
import net.mguenther.kafkasampler.gtd.persistence.ItemEventProcessor;
import net.mguenther.kafkasampler.gtd.persistence.ItemEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Configuration
public class GtdContext {

    @Bean
    public ItemEventProducer itemEventProducer(@Autowired ItemEventCodec codec,
                                               @Autowired GtdConfig config) {
        final ProducerSettings<ItemEvent, byte[]> settings = ProducerSettings
                .builder(codec)
                .usingBootstrapServer(config.getBrokerUrl())
                .build();
        return new ItemEventProducer("item-event-producer", config.getTopic(), settings);
    }

    @Bean
    public ItemEventConsumer itemEventConsumer(@Autowired ItemEventCodec codec,
                                               @Autowired ItemEventProcessor processor,
                                               @Autowired GtdConfig config) {
        final ConsumerSettings<byte[], ItemEvent> settings = ConsumerSettings
                .builder(config.getApplicationName(), codec, processor)
                .usingBootstrapServer(config.getBrokerUrl())
                .build();
        return new ItemEventConsumer(config.getApplicationName(), config.getTopic(), settings);
    }

    @Bean
    public ItemEventCodec itemEventCodec(@Autowired AvroDomainConverter converter) {
        return new ItemEventCodec(converter);
    }

    @Bean
    public AvroDomainConverter avroDomainConverter() {
        return new AvroDomainConverter();
    }

    @Bean
    public ItemEventProcessor itemEventProcessor(@Autowired EventReceiver eventReceiver) {
        return new ItemEventProcessor(eventReceiver);
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    public CommandLineRunner schedulingRunner(@Autowired TaskExecutor executor,
                                              @Autowired ItemEventConsumer consumer) {
        return args -> executor.execute(consumer);
    }
}
