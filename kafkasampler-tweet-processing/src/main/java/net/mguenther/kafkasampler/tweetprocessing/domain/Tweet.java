package net.mguenther.kafkasampler.tweetprocessing.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.mguenther.kafkasampler.tweetprocessing.util.UtcIso8601Deserializer;
import net.mguenther.kafkasampler.tweetprocessing.util.UtcIso8601Serializer;

import java.io.IOException;
import java.util.Date;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Getter
@EqualsAndHashCode(of = "tweetId")
@ToString
public class Tweet {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final long tweetId;

    private final String text;

    private final int numberOfRetweets;

    private final int numberOfFavorites;

    private final Date createdAt;

    private final User user;

    private Location location;

    @JsonCreator
    public Tweet(@JsonProperty("tweetId") final long tweetId,
                 @JsonProperty("text") final String text,
                 @JsonProperty("numberOfRetweets") final int numberOfRetweets,
                 @JsonProperty("numberOfFavorites") final int numberOfFavorites,
                 @JsonProperty("createdAt") @JsonSerialize(using = UtcIso8601Serializer.class) @JsonDeserialize(using = UtcIso8601Deserializer.class) final Date createdAt,
                 @JsonProperty("user") final User user,
                 @JsonProperty("location") final Location location) {
        this.tweetId = tweetId;
        this.text = text;
        this.numberOfRetweets = numberOfRetweets;
        this.numberOfFavorites = numberOfFavorites;
        this.createdAt = createdAt;
        this.user = user;
        this.location = location;
    }

    public String toJson() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (IOException e) {
            throw new RuntimeException("Unable to serialize Tweet to JSON string.", e);
        }
    }

    public static Tweet fromJson(final String tweetAsJson) {
        try {
            final Tweet tweet = MAPPER.readValue(tweetAsJson, Tweet.class);
            return tweet;
        } catch (IOException e) {
            throw new RuntimeException("Unable to deserialize JSON string to Tweet.", e);
        }
    }
}
