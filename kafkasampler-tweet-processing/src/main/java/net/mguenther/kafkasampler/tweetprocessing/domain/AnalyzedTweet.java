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
public class AnalyzedTweet {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @JsonProperty("tweetId")
    private final long tweetId;

    @JsonProperty("text")
    private final String text;

    @JsonProperty("numberOfRetweets")
    private final int numberOfRetweets;

    @JsonProperty("numberOfFavorites")
    private final int numberOfFavorites;

    @JsonProperty("createdAt")
    @JsonSerialize(using = UtcIso8601Serializer.class)
    @JsonDeserialize(using = UtcIso8601Deserializer.class)
    private final Date createdAt;

    @JsonProperty("user")
    private final User user;

    @JsonProperty("sentiment")
    private final Sentiment sentiment;

    @JsonProperty("location")
    private Location location;

    @JsonCreator
    public AnalyzedTweet(@JsonProperty("tweetId") final long tweetId,
                         @JsonProperty("text") final String text,
                         @JsonProperty("numberOfRetweets") final int numberOfRetweets,
                         @JsonProperty("numberOfFavorites") final int numberOfFavorites,
                         @JsonProperty("createdAt") @JsonSerialize(using = UtcIso8601Serializer.class) @JsonDeserialize(using = UtcIso8601Deserializer.class) final Date createdAt,
                         @JsonProperty("user") final User user,
                         @JsonProperty("sentiment") final Sentiment sentiment,
                         @JsonProperty("location") final Location location) {
        this.tweetId = tweetId;
        this.text = text;
        this.numberOfRetweets = numberOfRetweets;
        this.numberOfFavorites = numberOfFavorites;
        this.createdAt = createdAt;
        this.user = user;
        this.sentiment = sentiment;
        this.location = location;
    }

    public String toJson() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (IOException e) {
            throw new RuntimeException("Unable to serialize AnalyzedTweet to JSON string.", e);
        }
    }

    public static AnalyzedTweet fromJson(final String analyzedTweetAsJson) {
        try {
            final AnalyzedTweet analyzedTweet = MAPPER.readValue(analyzedTweetAsJson, AnalyzedTweet.class);
            return analyzedTweet;
        } catch (IOException e) {
            throw new RuntimeException("Unable to deserialize JSON string to AnalyzedTweet.", e);
        }
    }
}
