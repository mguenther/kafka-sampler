package net.mguenther.kafkasampler.tweetprocessing.ingest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@RequiredArgsConstructor
public class IngestOverview {

    @JsonProperty
    private final List<Ingest> ingests;
}
