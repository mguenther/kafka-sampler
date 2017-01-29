package net.mguenther.kafkasampler.tweetprocessing.enrichment;

import net.mguenther.kafkasampler.tweetprocessing.domain.Sentiment;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public interface SentimentAnalyzer {

    Sentiment analyze(String text);
}
