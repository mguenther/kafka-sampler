package net.mguenther.kafkasampler.tweetprocessing.sanitizing;

import net.mguenther.kafkasampler.tweetprocessing.domain.Tweet;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public class DeduplicationFilterTest {

    private TweetDeduplicationFilter subjectUnderTest;

    @Before
    public void prepareTests() {
        this.subjectUnderTest = new TweetDeduplicationFilter();
    }

    @Test
    public void seenBeforeShouldYieldFalseForTheFirstTimeAndTrueForTheNextStatusWithSameId() {
        // TODO (mgu): Use a generator!
        final Tweet tweet = new Tweet(1, null, 0, 0, null, null, null);
        assertFalse(subjectUnderTest.seenBefore(tweet));
        assertTrue(subjectUnderTest.seenBefore(tweet));
    }
}
