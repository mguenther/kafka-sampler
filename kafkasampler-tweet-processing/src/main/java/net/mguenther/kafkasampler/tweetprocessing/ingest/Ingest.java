package net.mguenther.kafkasampler.tweetprocessing.ingest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import rx.Subscription;

import java.util.List;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@RequiredArgsConstructor
@EqualsAndHashCode(of = "ingestId")
@Getter
@ToString
@JsonIgnoreProperties(value = "subscription")
public class Ingest {

    @JsonProperty
    private final String ingestId;
    @JsonProperty
    private final List<String> keywords;
    private final Subscription subscription;
}
