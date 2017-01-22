package net.mguenther.kafkasampler.adapter.kafka;

import net.mguenther.kafkasampler.adapter.kafka.topic.TopicClient;
import net.mguenther.kafkasampler.adapter.kafka.topic.TopicSettings;
import net.mguenther.kafkasampler.adapter.kafka.topic.ZkUtilsFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public class ProduceAndConsumeMessageIT {

    private static final String TEST_TOPIC = "kafkasampler-test-topic-1";

    private static final String PRODUCER_ID = "prod-ProduceAndConsumeMessage";

    private static final String CONSUMER_ID = "cons-ProduceAndConsumeMessage";

    private static final String GROUP_ID = "cgrp-ProduceAndConsumeMessage";

    private static final String TEST_MESSAGE = "test-message";

    private static final TopicClient TOPIC_CLIENT = new TopicClient(new ZkUtilsFactory(ZooKeeperSettings.usingDefaults()));

    @BeforeClass
    public static void prepareTests() throws Exception {
        // it is okay to use a blocking call at this point since we want to delay the test
        // execution util the topic has been created
        TOPIC_CLIENT
                .exists(TEST_TOPIC)
                .thenCompose(exists -> TOPIC_CLIENT.createTopic(TopicSettings.useDefaults(TEST_TOPIC)))
                .get();
    }

    @AfterClass
    public static void removeTopicAfterTests() throws Exception {
        TOPIC_CLIENT.deleteTopic(TEST_TOPIC).get();
    }

    @Test
    public void loggedMessageShouldBeConsumedAfterwards() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final Codec<String, String> codec = new StringIdentityCodec();
        final InstrumentingStringProcessor processor = new InstrumentingStringProcessor(latch);
        final Producer<String, String> producer = new Producer<>(
                PRODUCER_ID, ProducerSettings.usingDefaults(codec));
        final Consumer<String, String> consumer = new Consumer<>(
                CONSUMER_ID, TEST_TOPIC, ConsumerSettings.usingDefaults(GROUP_ID, codec, processor));

        producer.log(TEST_TOPIC, TEST_MESSAGE);

        final Thread consumerThread = new Thread(consumer);
        consumerThread.start();

        latch.await(30, TimeUnit.SECONDS);

        assertThat(
                "The consumer should delegated exactly one message to the processor.",
                processor.getNumberOfConsumedMessages(), is(1));
        assertTrue(
                "The processor should have seen the message we logged to the topic.",
                processor.hasConsumedMessage(s -> s.equals(TEST_MESSAGE)));

        consumer.stop();
        consumerThread.join();
    }

    static class InstrumentingStringProcessor implements Processor<String> {

        private final List<String> consumedMessages = new ArrayList<>();

        private final CountDownLatch latch;

        public InstrumentingStringProcessor(final CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public CompletableFuture<Void> process(final String message) {
            return CompletableFuture.runAsync(() -> {
                consumedMessages.add(message);
                latch.countDown();
            });
        }

        public List<String> getConsumedMessages() {
            return Collections.unmodifiableList(consumedMessages);
        }

        public int getNumberOfConsumedMessages() {
            return consumedMessages.size();
        }

        public boolean hasConsumedMessage(final Predicate<String> predicate) {
            return consumedMessages.stream().anyMatch(predicate);
        }
    }
}
