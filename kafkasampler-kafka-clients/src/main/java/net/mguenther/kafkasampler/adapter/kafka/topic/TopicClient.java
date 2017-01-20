package net.mguenther.kafkasampler.adapter.kafka.topic;

import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.common.TopicAlreadyMarkedForDeletionException;
import kafka.utils.ZkUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.errors.InvalidTopicException;
import org.apache.kafka.common.errors.TopicExistsException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Slf4j
public class TopicClient {

    private final ExecutorService executor;

    private final ZkUtilsFactory factory;

    public TopicClient(final ZkUtilsFactory factory) {
        this(factory, ForkJoinPool.commonPool());
    }

    public TopicClient(final ZkUtilsFactory factory, final ExecutorService executor) {
        this.factory = factory;
        this.executor = executor;
    }

    /**
     * Creates the topic as defined by the given {@code TopicSettings} asynchronously. The
     * returned {@code CompletableFuture} may complete exceptionally in case the given topic
     * settings are not admissible, the topic in question already exists or there was some
     * other unforeseen error during topic creation.
     *
     * @param topicSettings
     *      provides the settings for the topic to create
     * @return
     *      {@code CompletableFuture} closing over the state of the asynchronous execution;
     *      the caller can or cannot block until the operation concludes
     */
    public CompletableFuture<Void> createTopic(final TopicSettings topicSettings) {
        return CompletableFuture.runAsync(() -> {
            ZkUtils zkUtils = null;
            try {
                zkUtils = factory.get();
                AdminUtils.createTopic(
                        zkUtils,
                        topicSettings.getTopic(),
                        topicSettings.getNumberOfPartitions(),
                        topicSettings.getNumberOfReplicas(),
                        topicSettings.getProperties(),
                        RackAwareMode.Enforced$.MODULE$);
                log.info("Created topic {} with settings {}.", topicSettings.getTopic(), topicSettings);
            } catch (IllegalArgumentException | ConfigException | InvalidTopicException e) {
                throw new CompletionException("Invalid topic settings.", e);
            } catch (TopicExistsException e) {
                throw new CompletionException("The topic already exists.", e);
            } catch (Exception e) {
                throw new CompletionException("Topic creation failed.", e);
            } finally {
                if (zkUtils != null) {
                    zkUtils.close();
                }
            }
        }, executor);
    }

    /**
     * Marks the given {@code topic} for deletion. Please note that topics are not immediately
     * deleted from a Kafka cluster. This method will fail if the configuration of Kafka brokers
     * that comprise the Kafka cluster prohibits topic deletions. The returned {@code CompletableFuture}
     * may complete exceptionally in this case or if the topic has already been marked for deletion
     * or if there occured some other unforeseen error that during topic deletion.
     *
     * @param topic
     *      the topic that ought to be deleted
     * @return
     *      {@code CompletableFuture} closing over the state of the asynchronous execution;
     *      the caller can or cannot block until the operation concludes
     */
    public CompletableFuture<Void> deleteTopic(final String topic) {
        return CompletableFuture.runAsync(() -> {
            ZkUtils zkUtils = null;
            try {
                zkUtils = factory.get();
                AdminUtils.deleteTopic(zkUtils, topic);
                log.info("Marked topic {} for deletion.", topic);
            } catch (TopicAlreadyMarkedForDeletionException e) {
                throw new CompletionException("Topic " + topic + " has already been marked for deletion.", e);
            } catch (Exception e) {
                throw new CompletionException("Unable to delete topic " + topic + ".", e);
            } finally {
                if (zkUtils != null) {
                    zkUtils.close();
                }
            }
        }, executor);
    }

    /**
     * Determines whether the given {@code topic} exists. The returned {@code CompletableFuture}
     * may complete exceptionally if there has been an unforeseen error while checking if the
     * topic exists.
     *
     * @param topic
     *      the topic that ought to be checked
     * @return
     *      {@code CompletableFuture} closing over the state of the asynchronous execution:
     *      the caller can or cannot block until the operation concludes
     */
    public CompletableFuture<Boolean> exists(final String topic) {
        return CompletableFuture.supplyAsync(() -> {
            ZkUtils zkUtils = null;
            try {
                zkUtils = factory.get();
                log.info("Trying to determine whether topic {} exists.", topic);
                return AdminUtils.topicExists(zkUtils, topic);
            } catch (Exception e) {
                throw new CompletionException("Unable to query the state of topic " + topic + ".", e);
            }
        }, executor);
    }
}
