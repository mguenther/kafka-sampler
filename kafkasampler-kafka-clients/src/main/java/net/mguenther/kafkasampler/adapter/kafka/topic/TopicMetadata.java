package net.mguenther.kafkasampler.adapter.kafka.topic;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Represents metadata on partitions and replicas of a topic.
 *
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(of = "topic")
@ToString
public class TopicMetadata {

    private final String topic;
    private final int numberOfPartitions;
    private final int numberOfReplicas;

    /**
     * Although the number of partitions must always be > 0, the number of replicas
     * may be 0 for a short amount of time after topic creation until leaders are assigned
     * to all partitions. As soon as leaders have been elected, numberOfReplicas will
     * be > 0.
     *
     * @return
     *      {@code true} if leaders have been elected (inferred from the returned
     *      number of replicas), {@code false} otherwise
     */
    public boolean leadersElected() {
        return numberOfReplicas == 0;
    }
}
