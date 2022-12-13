package com.lostsidewalk.buffy.rss;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RSSPublisherConfig {

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
