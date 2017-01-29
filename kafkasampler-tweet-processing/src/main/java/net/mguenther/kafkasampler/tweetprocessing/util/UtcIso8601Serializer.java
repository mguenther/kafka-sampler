package net.mguenther.kafkasampler.tweetprocessing.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.util.ISO8601Utils;

import java.io.IOException;
import java.util.Date;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public class UtcIso8601Serializer extends JsonSerializer<Date> {

    @Override
    public void serialize(final Date date,
                          final JsonGenerator jsonGenerator,
                          final SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeString(ISO8601Utils.format(date));
    }
}
