package net.mguenther.kafkasampler.streams.advertisements;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.ToString;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Getter
@ToString
public class AdvertisementClicked {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @JsonProperty("advertisementId")
    private final String advertisementId;

    @JsonProperty("referrer")
    private final String referrer;

    @JsonProperty("timestamp")
    private final long timestamp;

    public AdvertisementClicked(final String advertisementId, final String referrer) {
        this.advertisementId = advertisementId;
        this.referrer = referrer;
        this.timestamp = Instant.now(Clock.systemUTC()).toEpochMilli();
    }

    @JsonCreator
    public AdvertisementClicked(@JsonProperty("advertisementId") final String advertisementId,
                                @JsonProperty("referrer") final String referrer,
                                @JsonProperty("timestamp") final long timestamp) {
        this.advertisementId = advertisementId;
        this.referrer = referrer;
        this.timestamp = timestamp;
    }

    public String toJson() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (IOException e) {
            throw new RuntimeException("Unable to serialize instance of AdvertisementClicked to JSON string.", e);
        }
    }

    public static AdvertisementClicked fromJson(final String json) {
        try {
            return MAPPER.readValue(json, AdvertisementClicked.class);
        } catch (IOException e) {
            throw new RuntimeException("Unable to deserialize JSON string to instance of AdvertisementClicked.", e);
        }
    }
}
