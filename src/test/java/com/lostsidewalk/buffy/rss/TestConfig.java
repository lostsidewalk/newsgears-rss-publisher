package com.lostsidewalk.buffy.rss;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties
@Configuration
class TestConfig {

    @Autowired
    RSSPublisherConfigProps configProps;

    @Bean
    RSSChannelBuilder rssChannelBuilder() {
        return new RSSChannelBuilder(configProps);
    }

    @Bean
    ATOMFeedBuilder atomFeedBuilder() {
        return new ATOMFeedBuilder(configProps);
    }
}
