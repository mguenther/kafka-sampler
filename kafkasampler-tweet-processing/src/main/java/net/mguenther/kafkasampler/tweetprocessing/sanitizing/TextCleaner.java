package net.mguenther.kafkasampler.tweetprocessing.sanitizing;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public class TextCleaner {

    private static final String PATTERN_URL = "((www\\.[^\\s]+)|(https?://[^\\s]+))";

    private static final String PATTERN_USER_NAME = "@[^\\s]+";

    private static final String PATTERN_HASHTAG = "#";

    private static final String PATTERN_PUNCTUATION = "\\p{Punct}+";

    public String sanitize(final String tweetText) {
        return tweetText
                .toLowerCase()
                .replaceAll(PATTERN_URL, "")
                .replaceAll(PATTERN_USER_NAME, "")
                .replaceAll(PATTERN_HASHTAG, "")
                .replaceAll(PATTERN_PUNCTUATION, "");
    }
}
