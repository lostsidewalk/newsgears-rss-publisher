package com.lostsidewalk.buffy.rss;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


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

    //
    //
    //

    public int getChannelImageHeight() {
        return channelImageHeight;
    }

    public void setChannelImageHeight(int channelImageHeight) {
        this.channelImageHeight = channelImageHeight;
    }

    public int getChannelImageWidth() {
        return channelImageWidth;
    }

    public void setChannelImageWidth(int channelImageWidth) {
        this.channelImageWidth = channelImageWidth;
    }

    public String getChannelLinkTemplate() {
        return channelLinkTemplate;
    }

    public void setChannelLinkTemplate(String channelLinkTemplate) {
        this.channelLinkTemplate = channelLinkTemplate;
    }

    public String getChannelUriTemplate() {
        return channelUriTemplate;
    }

    public void setChannelUriTemplate(String channelUriTemplate) {
        this.channelUriTemplate = channelUriTemplate;
    }

    public String getChannelImageUrlTemplate() {
        return channelImageUrlTemplate;
    }

    public void setChannelImageUrlTemplate(String channelImageUrlTemplate) {
        this.channelImageUrlTemplate = channelImageUrlTemplate;
    }

    public String getRssFeedType() {
        return rssFeedType;
    }

    public void setRssFeedType(String rssFeedType) {
        this.rssFeedType = rssFeedType;
    }

    public String getAtomFeedType() {
        return atomFeedType;
    }

    public void setAtomFeedType(String atomFeedType) {
        this.atomFeedType = atomFeedType;
    }

    public int getChannelTtl() {
        return channelTtl;
    }

    public void setChannelTtl(int channelTtl) {
        this.channelTtl = channelTtl;
    }
}
