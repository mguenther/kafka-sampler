package net.mguenther.kafkasampler.tweetprocessing.ingest;

import net.mguenther.kafkasampler.tweetprocessing.domain.Location;
import net.mguenther.kafkasampler.tweetprocessing.domain.Tweet;
import net.mguenther.kafkasampler.tweetprocessing.domain.User;
import twitter4j.Status;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public class StatusToTweetConverter {

    public Tweet convert(final Status status) {

        final Location location = hasGeoLocation(status) ? toLocation(status) : null;

        return new Tweet(
                status.getId(),
                status.getText(),
                status.getRetweetCount(),
                status.getFavoriteCount(),
                status.getCreatedAt(),
                toUser(status),
                location);
    }

    private boolean hasGeoLocation(final Status status) {
        return status.getUser() != null && status.getUser().isGeoEnabled() && status.getGeoLocation() != null;
    }

    private User toUser(final Status status) {

        return new User(
                status.getUser().getId(),
                status.getUser().getName(),
                status.getUser().getScreenName(),
                status.getUser().getLocation(),
                status.getUser().getFollowersCount());
    }

    private Location toLocation(final Status status) {

        return new Location(
                status.getGeoLocation().getLatitude(),
                status.getGeoLocation().getLongitude());
    }
}
