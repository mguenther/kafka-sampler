package net.mguenther.kafkasampler.adapter.kafka;

import org.apache.kafka.common.serialization.Serializer;

import java.util.Optional;

/**
 * A {@code Encoder} knows how to encode instances of type {@code InputType} to instances
 * of type {@code OutputType}. The {@code OutputType} is the type that needs to be serialized
 * by Kafka. To ensure that the underlying Kafka serializer is compatible with type
 * {@code OutputType}, implementing classes need to override {@link Encoder#underlyingKafkaSerializer()}
 * using the appropriate Kafka {@link Serializer}.
 *
 * The interface does not make any assumptions on the thread-safety of its implementations.
 * Thread-safety has to be ensured by clients of this interface.
 *
 * @param <InputType>
 *     represents the input type
 * @param <OutputType>
 *     represents the resulting type after encoding
 *
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public interface Encoder<InputType, OutputType> {
    /**
     * Encodes the given input of type {@code InputType} to an output of type {@code OutputType}.
     *
     * @param input
     *      instance of type {@code InputType}
     * @return
     *      instance of type {@code OutputType}, yielding the encoded data from the input object,
     *      {@code Optional#empty} if an error has been raised during the encoding process
     */
    Optional<OutputType> encode(InputType input);

    /**
     * @return
     *      The {@link Serializer} that is compatible with the output type {@code OutputType}
     *      of this {@code Encoder}
     */
    Class<? extends Serializer<OutputType>> underlyingKafkaSerializer();
}
