package net.mguenther.kafkasampler.adapter.elasticsearch;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Properties;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@RequiredArgsConstructor
@Getter
@ToString
public class ElasticsearchSettings {

    private static final String DEFAULT_HOST = "localhost";

    private static final int DEFAULT_PORT = 9300;

    private static final boolean DEFAULT_SNIFF = false;

    public static class ElasticsearchSettingsBuilder {

        private final String host;
        private final int port;
        private boolean sniff = DEFAULT_SNIFF;

        public ElasticsearchSettingsBuilder() {
            this(DEFAULT_HOST, DEFAULT_PORT);
        }

        public ElasticsearchSettingsBuilder(final String host, final int port) {
            this.host = host;
            this.port = port;
        }

        public ElasticsearchSettingsBuilder enableTransportSniffing() {
            this.sniff = true;
            return this;
        }

        public ElasticsearchSettingsBuilder disableTransportSniffing() {
            this.sniff = false;
            return this;
        }

        public ElasticsearchSettings build() {
            return new ElasticsearchSettings(host, port, sniff);
        }
    }

    private final String host;

    private final int port;

    private final boolean sniffingActivated;

    public static ElasticsearchSettingsBuilder builder(final String host, final int port) {
        return new ElasticsearchSettingsBuilder(host, port);
    }

    public static ElasticsearchSettings usingDefaults(final String host, final int port) {
        return new ElasticsearchSettingsBuilder(host, port).build();
    }
}
