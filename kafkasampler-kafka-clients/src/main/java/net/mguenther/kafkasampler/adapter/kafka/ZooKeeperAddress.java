package net.mguenther.kafkasampler.adapter.kafka;

import lombok.RequiredArgsConstructor;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@RequiredArgsConstructor
public class ZooKeeperAddress {

    private final String hostname;

    private final int port;

    public String getAddress() {
        return hostname.concat(":").concat(String.valueOf(port));
    }
}
