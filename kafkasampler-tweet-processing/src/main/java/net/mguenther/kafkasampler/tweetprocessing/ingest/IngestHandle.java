package net.mguenther.kafkasampler.tweetprocessing.ingest;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class IngestHandle {

    private final List<String> keywords;

    public String[] getKeywordsAsArray() {
        final String[] asArray = new String[keywords.size()];
        for (int i = 0; i < keywords.size(); i++) {
            asArray[i] = keywords.get(i);
        }
        return asArray;
    }
}
