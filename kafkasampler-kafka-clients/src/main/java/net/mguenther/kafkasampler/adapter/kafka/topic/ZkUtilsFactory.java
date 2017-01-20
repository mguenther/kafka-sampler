package net.mguenther.kafkasampler.adapter.kafka.topic;

import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import lombok.RequiredArgsConstructor;
import net.mguenther.kafkasampler.adapter.kafka.ZooKeeperSettings;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@RequiredArgsConstructor
public class ZkUtilsFactory {

    private final ZooKeeperSettings settings;

    public ZkUtils get() {
        if (settings.getZooKeeperAddresses().isEmpty()) {
            throw new IllegalStateException("There are no ZooKeeper instances configured to connect to.");
        }
        final String zkAddress = settings.getZooKeeperAddresses().get(0).getAddress();
        final ZkClient zkClient = new ZkClient(
                zkAddress,
                settings.getSessionTimeout(),
                settings.getConnectionTimeout(),
                ZKStringSerializer$.MODULE$);
        final ZkConnection zkConnection = new ZkConnection(zkAddress);
        return new ZkUtils(zkClient, zkConnection, false);
    }
}
