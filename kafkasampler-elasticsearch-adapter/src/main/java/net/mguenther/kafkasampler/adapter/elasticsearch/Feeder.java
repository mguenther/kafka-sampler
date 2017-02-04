package net.mguenther.kafkasampler.adapter.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Slf4j
public class Feeder<T extends TypedDocument> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Client client;

    private final String index;

    public Feeder(final ClientFactory factory, final String index) {
        this.client = factory.get();
        this.index = index;
    }

    public boolean index(final T document) {
        return index(Collections.singletonList(document));
    }

    public boolean index(final Collection<T> documents) {
        boolean result = true;
        try {
            createIndexIfNonExisting();
            final BulkRequestBuilder builder = client.prepareBulk();
            for (T document : documents) {
                builder.add(toIndexRequestBuilder(document));
            }
            builder.execute().get();
            log.info("Successfully indexed batch of {} documents.", documents.size());
        } catch (Exception e) {
            log.warn("Caught an unexpected exception while indexing documents.", e);
            result = false;
        }
        return result;
    }

    private void createIndexIfNonExisting() {
        if (!indexExists()) {
            log.info("The index {} does not exist. Attempting to create it.");
            client.admin().indices().prepareCreate(index).get();
            log.info("Successfully created index {}.", index);
        }
    }

    private boolean indexExists() {
        return client.admin().indices().prepareExists(index).get().isExists();
    }

    private IndexRequestBuilder toIndexRequestBuilder(final T document) throws JsonProcessingException {
        final String json = MAPPER.writeValueAsString(document);
        final IndexRequestBuilder indexBuilder = client
                .prepareIndex(index, document.getType(), document.getId())
                .setContentType(XContentType.JSON)
                .setSource(json);
        return indexBuilder;
    }
}
