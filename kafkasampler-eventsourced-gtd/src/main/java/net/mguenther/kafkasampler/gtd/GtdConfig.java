package net.mguenther.kafkasampler.gtd;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Configuration
@Getter
public class GtdConfig {

    @Value("${gtd.applicationName}")
    private String applicationName;

    @Value("${gtd.topic}")
    private String topic;

    @Value("${gtd.zookeeperUrl}")
    private String zookeeperUrl;

    @Value("${gtd.brokerUrl}")
    private String brokerUrl;
}
