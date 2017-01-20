package net.mguenther.kafkasampler.adapter.kafka;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@RequiredArgsConstructor
@Getter
public class ZooKeeperSettings {

    public static final int DEFAULT_SESSION_TIMEOUT = 10_000;

    public static final int DEFAULT_CONNECTION_TIMEOUT = 8_000;

    public static final ZooKeeperAddress DEFAULT_ZOOKEEPER_ADDRESS = new ZooKeeperAddress("localhost", 2181);

    public static class ZooKeeperSettingsBuilder {

        private int sessionTimeout = DEFAULT_SESSION_TIMEOUT;
        private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
        private List<ZooKeeperAddress> zooKeeperAddresses = new ArrayList<>();

        public ZooKeeperSettingsBuilder withSessionTimeout(final int sessionTimeout) {
            this.sessionTimeout = sessionTimeout;
            return this;
        }

        public ZooKeeperSettingsBuilder withConnectionTimeout(final int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public ZooKeeperSettingsBuilder withZooKeeperAt(final String hostname, final int port) {
            this.zooKeeperAddresses.add(new ZooKeeperAddress(hostname, port));
            return this;
        }

        public ZooKeeperSettings build() {
            if (zooKeeperAddresses.isEmpty()) {
                zooKeeperAddresses.add(DEFAULT_ZOOKEEPER_ADDRESS);
            }
            return new ZooKeeperSettings(sessionTimeout, connectionTimeout, zooKeeperAddresses);
        }
    }

    private final int sessionTimeout;

    private final int connectionTimeout;

    private final List<ZooKeeperAddress> zooKeeperAddresses;

    public static ZooKeeperSettingsBuilder builder() {
        return new ZooKeeperSettingsBuilder();
    }
}
