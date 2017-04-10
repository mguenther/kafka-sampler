package net.mguenther.kafkasampler.streams.advertisements;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.ToString;

import java.io.IOException;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Getter
@ToString
public class MinuteByMinuteReport {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @JsonProperty("advertisementId")
    private final String advertisementId;

    @JsonProperty("description")
    private final String description;

    @JsonProperty("numberOfClicks")
    private final long numberOfClicks;

    @JsonCreator
    public MinuteByMinuteReport(@JsonProperty("advertisementId") final String advertisementId,
                                @JsonProperty("description") final String description,
                                @JsonProperty("numberOfClicks") final long numberOfClicks) {
        this.advertisementId = advertisementId;
        this.description = description;
        this.numberOfClicks = numberOfClicks;
    }

    public MinuteByMinuteReport(final Advertisement advertisement,
                                final long numberOfClicks) {
        this.advertisementId = advertisement.getAdvertisementId();
        this.description = advertisement.getDescription();
        this.numberOfClicks = numberOfClicks;
    }

    public String toJson() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (IOException e) {
            throw new RuntimeException("Unable to serialize instance of MinuteByMinuteReport to JSON string.", e);
        }
    }

    public static MinuteByMinuteReport fromJson(final String json) {
        try {
            return MAPPER.readValue(json, MinuteByMinuteReport.class);
        } catch (IOException e) {
            throw new RuntimeException("Unable to deserialize JSON string to instance of MinuteByMinuteReport.", e);
        }
    }
}