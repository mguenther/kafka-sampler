package net.mguenther.kafkasampler.tweetprocessing.sanitizing;

import org.junit.Before;
import org.junit.Test;
import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.RateLimitStatus;
import twitter4j.Scopes;
import twitter4j.Status;
import twitter4j.SymbolEntity;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;

import java.util.Date;
import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public class DeduplicationFilterTest {

    private StatusDeduplicationFilter subjectUnderTest;

    @Before
    public void prepareTests() {
        this.subjectUnderTest = new StatusDeduplicationFilter();
    }

    @Test
    public void seenBeforeShouldYieldFalseForTheFirstTimeAndTrueForTheNextStatusWithSameId() {
        final DummyStatus status = new DummyStatus();
        assertFalse(subjectUnderTest.seenBefore(status));
        assertTrue(subjectUnderTest.seenBefore(status));
    }

    class DummyStatus implements Status {

        private final long id;

        public DummyStatus() {
            final Random r = new Random();
            id = r.nextLong();
        }

        @Override
        public Date getCreatedAt() {
            return null;
        }

        @Override
        public long getId() {
            return id;
        }

        @Override
        public String getText() {
            return null;
        }

        @Override
        public int getDisplayTextRangeStart() {
            return 0;
        }

        @Override
        public int getDisplayTextRangeEnd() {
            return 0;
        }

        @Override
        public String getSource() {
            return null;
        }

        @Override
        public boolean isTruncated() {
            return false;
        }

        @Override
        public long getInReplyToStatusId() {
            return 0;
        }

        @Override
        public long getInReplyToUserId() {
            return 0;
        }

        @Override
        public String getInReplyToScreenName() {
            return null;
        }

        @Override
        public GeoLocation getGeoLocation() {
            return null;
        }

        @Override
        public Place getPlace() {
            return null;
        }

        @Override
        public boolean isFavorited() {
            return false;
        }

        @Override
        public boolean isRetweeted() {
            return false;
        }

        @Override
        public int getFavoriteCount() {
            return 0;
        }

        @Override
        public User getUser() {
            return null;
        }

        @Override
        public boolean isRetweet() {
            return false;
        }

        @Override
        public Status getRetweetedStatus() {
            return null;
        }

        @Override
        public long[] getContributors() {
            return new long[0];
        }

        @Override
        public int getRetweetCount() {
            return 0;
        }

        @Override
        public boolean isRetweetedByMe() {
            return false;
        }

        @Override
        public long getCurrentUserRetweetId() {
            return 0;
        }

        @Override
        public boolean isPossiblySensitive() {
            return false;
        }

        @Override
        public String getLang() {
            return null;
        }

        @Override
        public Scopes getScopes() {
            return null;
        }

        @Override
        public String[] getWithheldInCountries() {
            return new String[0];
        }

        @Override
        public long getQuotedStatusId() {
            return 0;
        }

        @Override
        public Status getQuotedStatus() {
            return null;
        }

        @Override
        public int compareTo(Status o) {
            return 0;
        }

        @Override
        public UserMentionEntity[] getUserMentionEntities() {
            return new UserMentionEntity[0];
        }

        @Override
        public URLEntity[] getURLEntities() {
            return new URLEntity[0];
        }

        @Override
        public HashtagEntity[] getHashtagEntities() {
            return new HashtagEntity[0];
        }

        @Override
        public MediaEntity[] getMediaEntities() {
            return new MediaEntity[0];
        }

        @Override
        public SymbolEntity[] getSymbolEntities() {
            return new SymbolEntity[0];
        }

        @Override
        public RateLimitStatus getRateLimitStatus() {
            return null;
        }

        @Override
        public int getAccessLevel() {
            return 0;
        }
    }
}
