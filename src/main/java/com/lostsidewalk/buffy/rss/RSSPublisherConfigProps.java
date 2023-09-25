package com.lostsidewalk.buffy.rss;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * The RSSPublisherConfigProps class holds configuration properties related to RSS feed publishing.
 * These properties are read from the application configuration and can be customized for RSS feed generation.
 */
@Configuration
@ConfigurationProperties(prefix = "rss.publisher")
public class RSSPublisherConfigProps {

    int channelImageHeight;
    int channelImageWidth;
    String channelLinkTemplate;
    String channelUriTemplate;
    String channelImageUrlTemplate;
    String rssFeedType;
    String atomFeedType;
    int channelTtl;

    /**
     * Gets the configured channel image height.
     *
     * @return The channel image height.
     */
    public int getChannelImageHeight() {
        return channelImageHeight;
    }

    /**
     * Sets the channel image height.
     *
     * @param channelImageHeight The channel image height to set.
     */
    @SuppressWarnings("unused")
    public void setChannelImageHeight(int channelImageHeight) {
        this.channelImageHeight = channelImageHeight;
    }

    /**
     * Gets the configured channel image width.
     *
     * @return The channel image width.
     */
    public int getChannelImageWidth() {
        return channelImageWidth;
    }

    /**
     * Sets the channel image width.
     *
     * @param channelImageWidth The channel image width to set.
     */
    @SuppressWarnings("unused")
    public void setChannelImageWidth(int channelImageWidth) {
        this.channelImageWidth = channelImageWidth;
    }

    /**
     * Gets the configured channel link template.
     *
     * @return The channel link template.
     */
    public String getChannelLinkTemplate() {
        return channelLinkTemplate;
    }

    /**
     * Sets the channel link template.
     *
     * @param channelLinkTemplate The channel link template to set.
     */
    @SuppressWarnings("unused")
    public void setChannelLinkTemplate(String channelLinkTemplate) {
        this.channelLinkTemplate = channelLinkTemplate;
    }

    /**
     * Gets the configured channel URI template.
     *
     * @return The channel URI template.
     */
    public String getChannelUriTemplate() {
        return channelUriTemplate;
    }

    /**
     * Sets the channel URI template.
     *
     * @param channelUriTemplate The channel URI template to set.
     */
    @SuppressWarnings("unused")
    public void setChannelUriTemplate(String channelUriTemplate) {
        this.channelUriTemplate = channelUriTemplate;
    }

    /**
     * Gets the configured channel image URL template.
     *
     * @return The channel image URL template.
     */
    public String getChannelImageUrlTemplate() {
        return channelImageUrlTemplate;
    }

    /**
     * Sets the channel image URL template.
     *
     * @param channelImageUrlTemplate The channel image URL template to set.
     */
    @SuppressWarnings("unused")
    public void setChannelImageUrlTemplate(String channelImageUrlTemplate) {
        this.channelImageUrlTemplate = channelImageUrlTemplate;
    }

    /**
     * Gets the configured RSS feed type.
     *
     * @return The RSS feed type.
     */
    public String getRssFeedType() {
        return rssFeedType;
    }

    /**
     * Sets the RSS feed type.
     *
     * @param rssFeedType The RSS feed type to set.
     */
    @SuppressWarnings("unused")
    public void setRssFeedType(String rssFeedType) {
        this.rssFeedType = rssFeedType;
    }

    /**
     * Gets the configured ATOM feed type.
     *
     * @return The ATOM feed type.
     */
    public String getAtomFeedType() {
        return atomFeedType;
    }

    /**
     * Sets the ATOM feed type.
     *
     * @param atomFeedType The ATOM feed type to set.
     */
    @SuppressWarnings("unused")
    public void setAtomFeedType(String atomFeedType) {
        this.atomFeedType = atomFeedType;
    }

    /**
     * Gets the configured channel time-to-live (TTL) value.
     *
     * @return The channel TTL value.
     */
    public int getChannelTtl() {
        return channelTtl;
    }

    /**
     * Sets the channel time-to-live (TTL) value.
     *
     * @param channelTtl The channel TTL value to set.
     */
    @SuppressWarnings("unused")
    public void setChannelTtl(int channelTtl) {
        this.channelTtl = channelTtl;
    }
}
