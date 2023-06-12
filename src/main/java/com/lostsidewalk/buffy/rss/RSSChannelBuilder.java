package com.lostsidewalk.buffy.rss;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lostsidewalk.buffy.post.StagingPost;
import com.lostsidewalk.buffy.queue.QueueDefinition;
import com.rometools.rome.feed.rss.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.CollectionUtils.size;
import static org.apache.commons.lang3.StringUtils.*;

class RSSChannelBuilder {

    private static final Gson GSON = new Gson();

    private final RSSPublisherConfigProps configProps;

    RSSChannelBuilder(RSSPublisherConfigProps configProps) {
        this.configProps = configProps;
    }

    //
    // FEED DEFINITION
    //

    Channel buildChannel(QueueDefinition queueDefinition, List<StagingPost> stagingPosts, Date pubDate) {
        Channel channel = new Channel();
        // feed type
        channel.setFeedType(this.configProps.getRssFeedType());
        // URI
        channel.setUri(String.format(configProps.getChannelUriTemplate(), queueDefinition.getTransportIdent()));
        // last build date
        Date lastBuildDate = stagingPosts.stream()
                .filter(s -> s.getLastUpdatedTimestamp() != null)
                .max(comparing(StagingPost::getLastUpdatedTimestamp))
                .map(StagingPost::getLastUpdatedTimestamp)
                .orElse(null);
        channel.setLastBuildDate(lastBuildDate);
        // pub date
        channel.setPubDate(pubDate);
        // required
        setChannelRequiredProperties(channel, queueDefinition);
        // optional
        JsonObject rssConfigObj = getRssConfigObj(queueDefinition);
        setChannelOptionalProperties(channel, rssConfigObj);
        // items
        channel.setItems(getItems(stagingPosts));

        return channel;
    }

    private void setChannelRequiredProperties(Channel channel, QueueDefinition queueDefinition) {
        channel.setTitle(queueDefinition.getTitle()); // GoUpstate.com News Headlines
        channel.setLink(String.format(configProps.getChannelLinkTemplate(), queueDefinition.getTransportIdent())); // http://www.goupstate.com/
        channel.setDescription(queueDefinition.getDescription()); //	The latest news from GoUpstate.com, a Spartanburg Herald-Journal Web site.
        channel.setTtl(configProps.getChannelTtl());
        channel.setLanguage(queueDefinition.getLanguage()); // en-us
        channel.setCopyright(queueDefinition.getCopyright()); // Copyright 2002, Spartanburg Herald-Journal
        channel.setGenerator(queueDefinition.getGenerator()); // MightyInHouse Content System v2.3
        channel.setImage(getChannelImage(queueDefinition));
    }

    private void setChannelOptionalProperties(Channel channel, JsonObject rssConfigObj) {
        if (rssConfigObj != null) {
            channel.setManagingEditor(getManagingEditor(rssConfigObj)); // geo@herald.com (George Matesky)
            channel.setWebMaster(getWebMaster(rssConfigObj)); // betty@herald.com (Betty Guernsey)
            channel.setDocs(getDocs(rssConfigObj)); // http://blogs.law.harvard.edu/tech/rss
            channel.setCloud(getCloud(rssConfigObj));
            channel.setRating(getRating(rssConfigObj)); // https://www.w3.org/PICS/
            channel.setSkipHours(getSkipHours(rssConfigObj));
            channel.setSkipDays(getSkipDays(rssConfigObj));
            channel.setCategories(getCategories(rssConfigObj));
        }
    }

    //
    //
    //

    private static JsonObject getRssConfigObj(QueueDefinition queueDefinition) {
        JsonObject exportConfigObj = Optional.ofNullable(queueDefinition.getExportConfig())
                .map(Object::toString)
                .map(s -> GSON.fromJson(s, JsonObject.class))
                .orElse(null);
        return (exportConfigObj != null && exportConfigObj.has("rssConfig")) ?
                exportConfigObj.get("rssConfig").getAsJsonObject() :
                null;
    }

    private Image getChannelImage(QueueDefinition queueDefinition) {
        Image image = new Image();
        image.setUrl(String.format(configProps.getChannelImageUrlTemplate(), queueDefinition.getTransportIdent())); // URL of the image
        image.setLink(String.format(configProps.getChannelUriTemplate(), queueDefinition.getTransportIdent())); // URL of the channel
        image.setTitle(queueDefinition.getTitle());
        image.setDescription(queueDefinition.getDescription());
        image.setHeight(configProps.getChannelImageHeight()); // height of the thumbnail we serve
        image.setWidth(configProps.getChannelImageWidth()); // width of the thumbnail we serve
        return image;
    }

    //
    //
    //

    private static String getStringProperty(JsonObject obj, String propertyName) {
        return obj == null ? null : obj.has(propertyName) ? obj.get(propertyName).getAsString() : null;
    }

    private static Integer getIntegerProperty(JsonObject obj, @SuppressWarnings("SameParameterValue") String propertyName) {
        return obj == null ? null : obj.has(propertyName) ? obj.get(propertyName).getAsNumber().intValue() : null;
    }

    private static String getManagingEditor(JsonObject rssConfigObj) {
        return getStringProperty(rssConfigObj, "managingEditor");
    }

    private static String getWebMaster(JsonObject rssConfigObj) {
        return getStringProperty(rssConfigObj, "webMaster");
    }

    private static String getDocs(JsonObject rssConfigObj) {
        return getStringProperty(rssConfigObj, "docs");
    }

    private static Cloud getCloud(JsonObject rssConfigObj) {
        String cloudPath = getStringProperty(rssConfigObj, "cloudPath");

        if (isNotBlank(cloudPath)) {
            String cloudDomain = getStringProperty(rssConfigObj, "cloudDomain");
            String cloudProtocol = getStringProperty(rssConfigObj, "cloudProtocol");
            String cloudRegisterProcedure = getStringProperty(rssConfigObj, "cloudRegisterProcedure");
            Integer cloudPort = getIntegerProperty(rssConfigObj, "cloudPort");

            Cloud cloud = new Cloud();
            cloud.setDomain(cloudDomain); // rpc.sys.com
            cloud.setPath(cloudPath); // /RPC2
            cloud.setPort(cloudPort == null ? 80 : cloudPort); // 80
            cloud.setProtocol(cloudProtocol); // xml-rpc
            cloud.setRegisterProcedure(cloudRegisterProcedure); // myCloud.rssPleaseNotify
            return cloud;
        }

        return null;
    }

    private static String getRating(JsonObject rssConfigObj) {
        return getStringProperty(rssConfigObj, "rating");
    }

    private static List<Integer> getSkipHours(JsonObject rssConfigObj) {
        String skiprHoursStr = getStringProperty(rssConfigObj, "skipHours"); // 0..23
        return isBlank(skiprHoursStr) ?
                emptyList() :
                stream(split(skiprHoursStr, ','))
                        .map(Integer::parseInt)
                        .collect(toList());
    }

    private static List<String> getSkipDays(JsonObject rssConfigObj) {
        String skipDaysStr = getStringProperty(rssConfigObj, "skipDays"); // Monday, Tuesday, Wednesday, Thursday, Friday, Saturday or Sunday
        return isBlank(skipDaysStr) ?
                emptyList() :
                stream(split(skipDaysStr, ',')).collect(toList());
    }

    private static List<Category> getCategories(JsonObject rssConfigObj) {
        String categoryValue = getStringProperty(rssConfigObj, "categoryValue");

        if (isNotBlank(categoryValue)) {
            Category category = new Category();
            category.setValue(categoryValue); // Grateful Dead
            category.setDomain(getStringProperty(rssConfigObj, "categoryDomain")); // http://www.fool.com/cusips
            return singletonList(category);
        }

        return null;
    }

    //
    //
    //

    private static List<Item> getItems(List<StagingPost> stagingPosts) {
        List<Item> items = null;
        if (isNotEmpty(stagingPosts)) {
            items = new ArrayList<>(size(stagingPosts));
            for (StagingPost stagingPost : stagingPosts) {
                Item item = RSSChannelItemBuilder.toItem(stagingPost);
                items.add(item);
            }
        }
        return items;
    }
}
