package net.mguenther.kafkasampler.tweetprocessing.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Getter
@EqualsAndHashCode(of = "userId")
@ToString
public class User {

    @JsonProperty("userId")
    private final long userId;

    @JsonProperty("name")
    private final String name;

    @JsonProperty("screenName")
    private final String screenName;

    @JsonProperty("location")
    private final String location;

    @JsonProperty("numberOfFollowers")
    private final int numberOfFollowers;

    @JsonCreator
    public User(@JsonProperty("userId") final long userId,
                @JsonProperty("name") final String name,
                @JsonProperty("screenName") final String screenName,
                @JsonProperty("location") final String location,
                @JsonProperty("numberOfFollowers") final int numberOfFollowers) {
        this.userId = userId;
        this.name = name;
        this.screenName = screenName;
        this.location = location;
        this.numberOfFollowers = numberOfFollowers;
    }
}
