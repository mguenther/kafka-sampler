package net.mguenther.kafkasampler.adapter.kafka;

import org.apache.kafka.common.serialization.Deserializer;

import java.util.Optional;

/**
 * A {@code Decoder} knows how to decode an input of type {@code InputType} to an output of
 * type {@code OutputType}. To ensure that the underlying Kafka serializer is compatible with
 * type {@code InputType}, implementing classes need to override {@link Decoder#underlyingKafkaDeserializer()}
 * using the appropriate Kafka {@link Deserializer}.
 *
 * This interface does not make any assumptions on the thread-safety of its implementations.
 * Thread-safety has to be ensured by clients of this interface.
 *
 * @param <InputType>
 *     represents the input type
 * @param <OutputType>
 *     represents the output type after decoding
 *
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public interface Decoder<InputType, OutputType> {

    /**
     * Decodes the given input of type {@code InputType} to an output of type {@code OutputType}.
     *
     * @param input
     *      instance of type {@code InputType}
     * @return
     *      instance of type {@code OutputType}, yielding the decoded data from the input object,
     *      {@code Optional#empty} if an error has been raised during the encoding process
     */
    Optional<OutputType> decode(InputType input);

    /**
     * @return
     *      The {@link Deserializer} that is compatible with the input type {@code InputType}
     *      of this {@code Decoder}
     */
    Class<? extends Deserializer<InputType>> underlyingKafkaDeserializer();
}
