package net.mguenther.kafkasampler.tweetprocessing.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.mguenther.kafkasampler.tweetprocessing.util.UtcIso8601Deserializer;
import net.mguenther.kafkasampler.tweetprocessing.util.UtcIso8601Serializer;

import java.io.IOException;
import java.util.Date;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = "tweetId")
@ToString
public class AnalyzedTweet {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final long tweetId;
    private final String text;
    private final int numberOfRetweets;
    private final int numberOfFavorites;
    @JsonSerialize(using = UtcIso8601Serializer.class)
    @JsonDeserialize(using = UtcIso8601Deserializer.class)
    private final Date createdAt;
    private final User user;
    private final Sentiment sentiment;
    private Location location;

    public String toJson() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (IOException e) {
            throw new RuntimeException("Unable to serialize AnalyzedTweet to JSON string.", e);
        }
    }
}
