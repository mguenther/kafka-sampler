package net.mguenther.kafkasampler.tweetprocessing.sanitizing;

import net.mguenther.kafkasampler.tweetprocessing.domain.Tweet;

/**
 * This filter eliminates unwanted tokens from the body of a tweet.
 *
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public class TweetSanitizer {

    private static final String PATTERN_URL = "((www\\.[^\\s]+)|(https?://[^\\s]+))";

    private static final String PATTERN_USER_NAME = "@[^\\s]+";

    private static final String PATTERN_HASHTAG = "#";

    private static final String PATTERN_PUNCTUATION = "\\p{Punct}+";

    private static final String PATTERN_RETWEET = "rt";

    public Tweet sanitize(final Tweet tweet) {
        return new Tweet(
                tweet.getTweetId(),
                sanitize(tweet.getText()),
                tweet.getNumberOfRetweets(),
                tweet.getNumberOfFavorites(),
                tweet.getCreatedAt(),
                tweet.getUser(),
                tweet.getLocation());
    }

    private String sanitize(final String tweetText) {
        return tweetText
                .toLowerCase()
                .replaceAll(PATTERN_URL, "")
                .replaceAll(PATTERN_USER_NAME, "")
                .replaceAll(PATTERN_HASHTAG, "")
                .replaceAll(PATTERN_PUNCTUATION, "")
                .replaceAll(PATTERN_RETWEET, "")
                .trim();
    }
}
