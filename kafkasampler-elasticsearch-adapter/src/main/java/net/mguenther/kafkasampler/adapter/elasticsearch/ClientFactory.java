package net.mguenther.kafkasampler.adapter.elasticsearch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@RequiredArgsConstructor
@Slf4j
public class ClientFactory {

    private final ElasticsearchSettings elasticSettings;

    public Client get() {
        Client client = null;
        try {
            final Settings settings = Settings
                    .builder()
                    .put("cluster.name", "elasticsearch")
                    .put("client.transport.sniff", elasticSettings.isSniffingActivated())
                    .build();
            /*final InetSocketTransportAddress transportAddress = new InetSocketTransportAddress(
                    InetAddress.getByName(elasticSettings.getHost()), elasticSettings.getPort());*/
            /*final TransportAddress transportAddress = new InetSocketTransportAddress(
                    InetAddress.getByAddress(new byte[] { (byte) 0, (byte) 0, (byte) 0, (byte) 0 }), 9300);
            client = new PreBuiltTransportClient(settings).addTransportAddress(transportAddress);*/

            final InetSocketTransportAddress transportAddress = new InetSocketTransportAddress(
                    InetAddress.getByAddress(new byte[] { (byte) 0, (byte) 0, (byte) 0, (byte) 0 }), 9300);
            client = TransportClient
                    .builder()
                    .settings(settings)
                    .build()
                    .addTransportAddress(transportAddress);
            log.info("Constructed transport client to {}:{}.", elasticSettings.getHost(), elasticSettings.getPort());
        } catch (UnknownHostException e) {
            log.warn("Could not construct Elasticsearch client for {} due to malformed configuration.", elasticSettings, e);
        }
        return client;
    }
}
