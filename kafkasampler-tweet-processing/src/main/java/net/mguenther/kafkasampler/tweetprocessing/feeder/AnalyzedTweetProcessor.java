package net.mguenther.kafkasampler.tweetprocessing.feeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mguenther.kafkasampler.adapter.elasticsearch.Feeder;
import net.mguenther.kafkasampler.adapter.kafka.Processor;
import net.mguenther.kafkasampler.tweetprocessing.domain.AnalyzedTweet;

import java.util.concurrent.CompletableFuture;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@RequiredArgsConstructor
@Slf4j
public class AnalyzedTweetProcessor implements Processor<AnalyzedTweet> {

    private final Feeder<AnalyzedTweetDocument> feeder;

    @Override
    public CompletableFuture<Void> process(final AnalyzedTweet message) {

        return CompletableFuture.runAsync(() -> {

            final AnalyzedTweetDocument document = new AnalyzedTweetDocument(
                    message.getTweetId(),
                    message.getText(),
                    message.getNumberOfRetweets(),
                    message.getNumberOfFavorites(),
                    message.getCreatedAt(),
                    message.getUser(),
                    message.getSentiment(),
                    message.getLocation());

            feeder.index(document);

        });
    }
}
