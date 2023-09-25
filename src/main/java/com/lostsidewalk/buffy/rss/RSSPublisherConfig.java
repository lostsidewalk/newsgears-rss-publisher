package com.lostsidewalk.buffy.rss;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The RSSPublisherConfig class is a Spring configuration class responsible for creating and configuring beans related to RSS feed publishing.
 * It uses properties from the {@link RSSPublisherConfigProps} class to configure the builders for RSS channels and ATOM feeds.
 */
@Configuration
public class RSSPublisherConfig {

    @Autowired
    RSSPublisherConfigProps configProps;

    /**
     * Creates a bean for the RSSChannelBuilder, which is responsible for building RSS channels based on configuration properties.
     *
     * @return An instance of {@link RSSChannelBuilder} configured with the properties from {@link RSSPublisherConfigProps}.
     */
    @Bean
    RSSChannelBuilder rssChannelBuilder() {
        return new RSSChannelBuilder(configProps);
    }

    /**
     * Creates a bean for the ATOMFeedBuilder, which is responsible for building ATOM feeds based on configuration properties.
     *
     * @return An instance of {@link ATOMFeedBuilder} configured with the properties from {@link RSSPublisherConfigProps}.
     */
    @Bean
    ATOMFeedBuilder atomFeedBuilder() {
        return new ATOMFeedBuilder(configProps);
    }
}
