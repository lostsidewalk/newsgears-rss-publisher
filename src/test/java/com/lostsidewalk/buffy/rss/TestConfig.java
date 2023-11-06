package com.lostsidewalk.buffy.rss;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@EnableConfigurationProperties
@Configuration
class TestConfig {

    @Autowired
    private RSSPublisherConfigProps configProps;

    @Bean
    RSSChannelBuilder rssChannelBuilder() {
        return new RSSChannelBuilder(configProps);
    }

    @Bean
    ATOMFeedBuilder atomFeedBuilder() {
        return new ATOMFeedBuilder(configProps);
    }

    @Override
    public String toString() {
        return "TestConfig{" +
                "configProps=" + configProps +
                '}';
    }
}
