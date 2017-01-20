package net.mguenther.kafkasampler.adapter.kafka;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@RequiredArgsConstructor
public class BrokerAddress {

    private final String hostname;

    private final int port;

    public String getAddress() {
        return hostname.concat(":").concat(String.valueOf(port));
    }
}
