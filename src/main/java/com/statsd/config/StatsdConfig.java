package com.statsd.config;

import com.statsd.client.StatsdClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Spring configuration file
 */

@Configuration
public class StatsdConfig {
    private
    @Value("${statsd.port}")
    String port;
    private
    @Value("${statsd.url}")
    String url;

    @Bean(name = "statsdClient", destroyMethod = "finalize")
    public StatsdClient statsdClient() throws UnknownHostException, IOException {
        int statsdPort = Integer.parseInt(port);
        StatsdClient statsdClient = new StatsdClient(url, statsdPort);
        statsdClient.enableMultiMetrics(true);
        statsdClient.startFlushTimer(2000);
        return statsdClient;
    }
}
