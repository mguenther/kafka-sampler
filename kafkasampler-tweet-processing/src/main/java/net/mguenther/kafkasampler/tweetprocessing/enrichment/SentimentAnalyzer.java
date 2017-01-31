package net.mguenther.kafkasampler.tweetprocessing.enrichment;

import net.mguenther.kafkasampler.tweetprocessing.domain.AnalyzedTweet;
import net.mguenther.kafkasampler.tweetprocessing.domain.Tweet;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public interface SentimentAnalyzer {

    AnalyzedTweet analyze(Tweet tweet);
}
