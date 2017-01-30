package net.mguenther.kafkasampler.tweetprocessing.enrichment;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mguenther.kafkasampler.tweetprocessing.domain.Sentiment;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@RequiredArgsConstructor
@Slf4j
public class CoreNlpSentimentAnalyzer implements SentimentAnalyzer {

    private final StanfordCoreNLP pipeline;

    @Override
    public Sentiment analyze(final String text) {

        if (StringUtils.isEmpty(text)) {
            log.warn("Unable to run sentiment analysis because the given text is empty.");
            return Sentiment.UNDEFINED;
        }

        final Annotation annotation = pipeline.process(text);
        final Comparator<AnalyzedSentence> bySentenceLength = (a, b) -> Integer.compare(a.sentence.length(), b.sentence.length());
        final Sentiment sentiment = annotation.get(CoreAnnotations.SentencesAnnotation.class)
                .stream()
                .map(sentence -> AnnotatedSentence.of(sentence, sentence.get(SentimentCoreAnnotations.AnnotatedTree.class)))
                .map(annotatedSentence -> AnalyzedSentence.of(annotatedSentence.sentence.toString(), RNNCoreAnnotations.getPredictedClass(annotatedSentence.tree)))
                .max(bySentenceLength)
                .map(analyzedSentence -> analyzedSentence.sentiment)
                .orElse(Sentiment.UNDEFINED);
        log.info("Calculated sentiment for text '{}' is {}.", text, sentiment);
        return sentiment;
    }

    @RequiredArgsConstructor
    private static class AnnotatedSentence {

        private final CoreMap sentence;
        private final Tree tree;

        public static AnnotatedSentence of(final CoreMap sentence, final Tree tree) {
            return new AnnotatedSentence(sentence, tree);
        }
    }

    @RequiredArgsConstructor
    private static class AnalyzedSentence implements Comparable<AnalyzedSentence> {

        private final String sentence;
        private final Sentiment sentiment;

        public static AnalyzedSentence of(final String sentence, final int sentiment) {
            return new AnalyzedSentence(sentence, toSentiment(sentiment));
        }

        private static Sentiment toSentiment(final int sentiment) {
            if (sentiment == 0 || sentiment == 1) {
                return Sentiment.NEGATIVE;
            } else if (sentiment == 2) {
                return Sentiment.NEUTRAL;
            } else if (sentiment == 3 || sentiment == 4) {
                return Sentiment.POSITIVE;
            } else {
                return Sentiment.UNDEFINED;
            }
        }

        @Override
        public int compareTo(final AnalyzedSentence o) {
            return sentence.length() - o.sentence.length();
        }
    }
}
