package net.mguenther.kafkasampler.streams.advertisements;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.IOException;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Getter
@ToString
@EqualsAndHashCode(of = { "advertisementId" })
public class Advertisement {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @JsonProperty("advertisementId")
    private final String advertisementId;

    @JsonProperty("description")
    private final String description;

    @JsonCreator
    public Advertisement(@JsonProperty("advertisementId") final String advertisementId,
                         @JsonProperty("description") final String description) {
        this.advertisementId = advertisementId;
        this.description = description;
    }

    public String toJson() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (IOException e) {
            throw new RuntimeException("Unable to serialize instance of Advertisement to JSON string.", e);
        }
    }

    public static Advertisement fromJson(final String json) {
        try {
            return MAPPER.readValue(json, Advertisement.class);
        } catch (IOException e) {
            throw new RuntimeException("Unable to deserialize JSON string to instance of Advertisement.", e);
        }
    }
}
