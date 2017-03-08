package net.mguenther.kafkasampler.adapter.elasticsearch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Getter
@EqualsAndHashCode(of = "id")
abstract public class TypedDocument {

    @JsonIgnore
    private final String id;

    @JsonIgnore
    private final String type;

    public TypedDocument(final String type) {
        this(UUID.randomUUID().toString(), type);
    }

    public TypedDocument(final String id, final String type) {
        this.id = id;
        this.type = type;
    }
}
