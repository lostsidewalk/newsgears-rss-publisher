package com.lostsidewalk.buffy.rss;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The RSSPublisherConfig class is a Spring configuration class responsible for creating and configuring beans related to RSS feed publishing.
 * It uses properties from the {@link RSSPublisherConfigProps} class to configure the builders for RSS channels and ATOM feeds.
 */
@Slf4j
@Configuration
public class RSSPublisherConfig {

    @Autowired
    RSSPublisherConfigProps configProps;

    /**
     * Default constructor; initializes the object.
     */
    RSSPublisherConfig() {
    }

    /**
     * Creates a bean for the RSSChannelBuilder, which is responsible for building RSS channels based on configuration properties.
     *
     * @return An instance of {@link RSSChannelBuilder} configured with the properties from {@link RSSPublisherConfigProps}.
     */
    @SuppressWarnings("DesignForExtension")
    @Bean
    RSSChannelBuilder rssChannelBuilder() {
        return new RSSChannelBuilder(configProps);
    }

    /**
     * Creates a bean for the ATOMFeedBuilder, which is responsible for building ATOM feeds based on configuration properties.
     *
     * @return An instance of {@link ATOMFeedBuilder} configured with the properties from {@link RSSPublisherConfigProps}.
     */
    @SuppressWarnings("DesignForExtension")
    @Bean
    ATOMFeedBuilder atomFeedBuilder() {
        return new ATOMFeedBuilder(configProps);
    }

    @Override
    public final String toString() {
        return "RSSPublisherConfig{" +
                "configProps=" + configProps +
                '}';
    }
}
