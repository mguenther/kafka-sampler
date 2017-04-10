package net.mguenther.kafkasampler.streams.advertisements;

import lombok.experimental.UtilityClass;

import java.util.UUID;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@UtilityClass
public class Naming {

    public static final String INSTANCE_ID = UUID.randomUUID().toString().substring(0, 4);

    public static final String PRODUCER_ID = "producer"; //String.format("producer-%s", INSTANCE_ID);

    public static final String TOPIC_AD_CLICKS = "topic-ads-clicks"; //String.format("ads-clicks-%s", INSTANCE_ID);

    public static final String TOPIC_AD_CLICKS_PER_MINUTE = "topic-ads-clicks-per-minute"; //String.format("ads-clicks-per-minute-%s", INSTANCE_ID);
}
