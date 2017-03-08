package net.mguenther.kafkasampler.tweetprocessing.ingest;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Component
@ApplicationScope
public class LinkScheme {

    private static final String INGESTS_URI = "/ingests";

    private static final String INGEST_URI = INGESTS_URI + "/{ingestId}";

    public URI toIngest(final String ingestId) {
        return UriBuilder.fromUri(INGEST_URI).resolveTemplate("ingestId", ingestId).build();
    }
}
