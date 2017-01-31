package net.mguenther.kafkasampler.tweetprocessing.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.util.ISO8601Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Slf4j
public class UtcIso8601Deserializer extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(final JsonParser jsonParser,
                            final DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        try {
            return ISO8601Utils.parse(jsonParser.getValueAsString(), new ParsePosition(0));
        } catch (ParseException e) {
            log.warn("Unable to parse ISO8601 date string {} to instance of java.util.Date.", jsonParser.getValueAsString());
            return null;
        }
    }
}
