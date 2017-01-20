package net.mguenther.kafkasampler.adapter.kafka;

/**
 * A {@code Codec} forms a bijective mapping between an {@code Encoder} and a {@code Decoder}.
 *
 * @param <InputType>
 *     represents the input type
 * @param <OutputType>
 *     represents the output type
 *
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public interface Codec<InputType, OutputType> extends Encoder<InputType, OutputType>, Decoder<OutputType, InputType> {
}
