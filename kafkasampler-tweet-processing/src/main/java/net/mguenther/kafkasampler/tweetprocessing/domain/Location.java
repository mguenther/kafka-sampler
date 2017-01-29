package net.mguenther.kafkasampler.tweetprocessing.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@RequiredArgsConstructor
@Getter
@ToString
public class Location {

    private final double latitude;
    private final double longitude;
}
