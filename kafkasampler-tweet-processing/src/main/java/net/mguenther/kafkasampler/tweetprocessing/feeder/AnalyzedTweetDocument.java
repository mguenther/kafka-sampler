package net.mguenther.kafkasampler.tweetprocessing.feeder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.mguenther.kafkasampler.adapter.elasticsearch.TypedDocument;
import net.mguenther.kafkasampler.tweetprocessing.domain.Location;
import net.mguenther.kafkasampler.tweetprocessing.domain.Sentiment;
import net.mguenther.kafkasampler.tweetprocessing.domain.User;
import net.mguenther.kafkasampler.tweetprocessing.util.UtcIso8601Deserializer;
import net.mguenther.kafkasampler.tweetprocessing.util.UtcIso8601Serializer;

import java.util.Date;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@ToString
@EqualsAndHashCode(callSuper = true)
public class AnalyzedTweetDocument extends TypedDocument {

    public static final String DOCUMENT_TYPE = "analyzed-tweet";

    @JsonProperty("tweetId")
    private final long tweetId;

    @JsonProperty("text")
    private final String text;

    @JsonProperty("numberOfRetweets")
    private final int numberOfRetweets;

    @JsonProperty("numberOfFavorites")
    private final int numberOfFavorites;

    @JsonSerialize(using = UtcIso8601Serializer.class)
    @JsonDeserialize(using = UtcIso8601Deserializer.class)
    @JsonProperty("createdAt")
    private final Date createdAt;

    @JsonProperty("user")
    private final User user;

    @JsonProperty("sentiment")
    private final Sentiment sentiment;

    @JsonProperty("location")
    private Location location;

    @JsonCreator
    public AnalyzedTweetDocument(@JsonProperty("tweetId") final long tweetId,
                                 @JsonProperty("text") final String text,
                                 @JsonProperty("numberOfRetweets") final int numberOfRetweets,
                                 @JsonProperty("numberOfFavorites") final int numberOfFavorites,
                                 @JsonProperty("createdAt") @JsonSerialize(using = UtcIso8601Serializer.class) @JsonDeserialize(using = UtcIso8601Deserializer.class) final Date createdAt,
                                 @JsonProperty("user") final User user,
                                 @JsonProperty("sentiment") final Sentiment sentiment,
                                 @JsonProperty("location") final Location location) {
        super(DOCUMENT_TYPE);
        this.tweetId = tweetId;
        this.text = text;
        this.numberOfRetweets = numberOfRetweets;
        this.numberOfFavorites = numberOfFavorites;
        this.createdAt = createdAt;
        this.user = user;
        this.sentiment = sentiment;
        this.location = location;
    }
}
