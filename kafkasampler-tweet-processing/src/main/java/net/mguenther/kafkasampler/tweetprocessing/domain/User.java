package net.mguenther.kafkasampler.tweetprocessing.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(of = "userId")
@ToString
public class User {

    private final long userId;
    private final String name;
    private final String screenName;
    private final String location;
    private final int numberOfFollowers;
}
