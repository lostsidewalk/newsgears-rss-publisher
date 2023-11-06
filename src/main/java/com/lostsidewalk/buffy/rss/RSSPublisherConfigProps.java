package com.lostsidewalk.buffy.rss;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * The RSSPublisherConfigProps class holds configuration properties related to RSS feed publishing.
 * These properties are read from the application configuration and can be customized for RSS feed generation.
 */
@Slf4j
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
    String defaultGeneratorValue;
    String defaultGeneratorUrl;
    String defaultGeneratorVersion;

    /**
     * Default constructor; initializes the object.
     */
    RSSPublisherConfigProps() {
    }

    /**
     * Gets the configured channel image height.
     *
     * @return The channel image height.
     */
    public final int getChannelImageHeight() {
        return channelImageHeight;
    }

    /**
     * Sets the channel image height.
     *
     * @param channelImageHeight The channel image height to set.
     */
    @SuppressWarnings("unused")
    public final void setChannelImageHeight(int channelImageHeight) {
        this.channelImageHeight = channelImageHeight;
    }

    /**
     * Gets the configured channel image width.
     *
     * @return The channel image width.
     */
    public final int getChannelImageWidth() {
        return channelImageWidth;
    }

    /**
     * Sets the channel image width.
     *
     * @param channelImageWidth The channel image width to set.
     */
    @SuppressWarnings("unused")
    public final void setChannelImageWidth(int channelImageWidth) {
        this.channelImageWidth = channelImageWidth;
    }

    /**
     * Gets the configured channel link template.
     *
     * @return The channel link template.
     */
    public final String getChannelLinkTemplate() {
        return channelLinkTemplate;
    }

    /**
     * Sets the channel link template.
     *
     * @param channelLinkTemplate The channel link template to set.
     */
    @SuppressWarnings("unused")
    public final void setChannelLinkTemplate(String channelLinkTemplate) {
        this.channelLinkTemplate = channelLinkTemplate;
    }

    /**
     * Gets the configured channel URI template.
     *
     * @return The channel URI template.
     */
    public final String getChannelUriTemplate() {
        return channelUriTemplate;
    }

    /**
     * Sets the channel URI template.
     *
     * @param channelUriTemplate The channel URI template to set.
     */
    @SuppressWarnings("unused")
    public final void setChannelUriTemplate(String channelUriTemplate) {
        this.channelUriTemplate = channelUriTemplate;
    }

    /**
     * Gets the configured channel image URL template.
     *
     * @return The channel image URL template.
     */
    public final String getChannelImageUrlTemplate() {
        return channelImageUrlTemplate;
    }

    /**
     * Sets the channel image URL template.
     *
     * @param channelImageUrlTemplate The channel image URL template to set.
     */
    @SuppressWarnings("unused")
    public final void setChannelImageUrlTemplate(String channelImageUrlTemplate) {
        this.channelImageUrlTemplate = channelImageUrlTemplate;
    }

    /**
     * Gets the configured RSS feed type.
     *
     * @return The RSS feed type.
     */
    public final String getRssFeedType() {
        return rssFeedType;
    }

    /**
     * Sets the RSS feed type.
     *
     * @param rssFeedType The RSS feed type to set.
     */
    @SuppressWarnings("unused")
    public final void setRssFeedType(String rssFeedType) {
        this.rssFeedType = rssFeedType;
    }

    /**
     * Gets the configured ATOM feed type.
     *
     * @return The ATOM feed type.
     */
    public final String getAtomFeedType() {
        return atomFeedType;
    }

    /**
     * Sets the ATOM feed type.
     *
     * @param atomFeedType The ATOM feed type to set.
     */
    @SuppressWarnings("unused")
    public final void setAtomFeedType(String atomFeedType) {
        this.atomFeedType = atomFeedType;
    }

    /**
     * Gets the configured channel time-to-live (TTL) value.
     *
     * @return The channel TTL value.
     */
    public final int getChannelTtl() {
        return channelTtl;
    }

    /**
     * Sets the channel time-to-live (TTL) value.
     *
     * @param channelTtl The channel TTL value to set.
     */
    @SuppressWarnings("unused")
    public final void setChannelTtl(int channelTtl) {
        this.channelTtl = channelTtl;
    }

    /**
     * Gets the configured default value for the channel generator.
     *
     * @return The default channel generator value.
     */
    public final String getDefaultGeneratorValue() {
        return defaultGeneratorValue;
    }

    /**
     * Sets the default value for the channel generator.
     *
     * @param defaultGeneratorValue The default channel generator value to set.
     */
    @SuppressWarnings("unused")
    public final void setDefaultGeneratorValue(String defaultGeneratorValue) {
        this.defaultGeneratorValue = defaultGeneratorValue;
    }

    /**
     * Gets the configured default URL for the channel generator.
     *
     * @return The default channel generator URL.
     */
    public final String getDefaultGeneratorUrl() {
        return defaultGeneratorUrl;
    }

    /**
     * Sets the default URL for the channel generator.
     *
     * @param defaultGeneratorUrl The default channel generator URL to set.
     */
    @SuppressWarnings("unused")
    public final void setDefaultGeneratorUrl(String defaultGeneratorUrl) {
        this.defaultGeneratorUrl = defaultGeneratorUrl;
    }

    /**
     * Gets the configured default version for the channel generator.
     *
     * @return The default channel generator version.
     */
    public final String getDefaultGeneratorVersion() {
        return defaultGeneratorVersion;
    }

    /**
     * Sets the default version for the channel generator.
     *
     * @param defaultGeneratorVersion The default channel generator version to set.
     */
    @SuppressWarnings("unused")
    public final void setDefaultGeneratorVersion(String defaultGeneratorVersion) {
        this.defaultGeneratorVersion = defaultGeneratorVersion;
    }

    @Override
    public final String toString() {
        return "RSSPublisherConfigProps{" +
                "channelImageHeight=" + channelImageHeight +
                ", channelImageWidth=" + channelImageWidth +
                ", channelLinkTemplate='" + channelLinkTemplate + '\'' +
                ", channelUriTemplate='" + channelUriTemplate + '\'' +
                ", channelImageUrlTemplate='" + channelImageUrlTemplate + '\'' +
                ", rssFeedType='" + rssFeedType + '\'' +
                ", atomFeedType='" + atomFeedType + '\'' +
                ", channelTtl=" + channelTtl +
                ", defaultGeneratorValue='" + defaultGeneratorValue + '\'' +
                ", defaultGeneratorUrl='" + defaultGeneratorUrl + '\'' +
                ", defaultGeneratorVersion='" + defaultGeneratorVersion + '\'' +
                '}';
    }
}
