package com.lostsidewalk.buffy.rss;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lostsidewalk.buffy.post.StagingPost;
import com.lostsidewalk.buffy.queue.QueueDefinition;
import com.rometools.rome.feed.rss.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.CollectionUtils.size;
import static org.apache.commons.lang3.StringUtils.*;


@Slf4j
class RSSChannelBuilder {

    private static final Gson GSON = new Gson();

    private final RSSPublisherConfigProps configProps;

    RSSChannelBuilder(RSSPublisherConfigProps configProps) {
        this.configProps = configProps;
    }

    //
    // FEED DEFINITION
    //

    final Channel buildChannel(QueueDefinition queueDefinition, Collection<? extends StagingPost> stagingPosts, Date pubDate) {
        Channel channel = new Channel();
        // feed type
        channel.setFeedType(configProps.getRssFeedType());
        // URI
        channel.setUri(String.format(configProps.getChannelUriTemplate(), queueDefinition.getTransportIdent()));
        // last build date
        Date lastBuildDate = stagingPosts.stream()
                .filter(stagingPost -> stagingPost.getLastUpdatedTimestamp() != null)
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
        // channel image
        Image channelImage = getChannelImage(queueDefinition);
        if (channelImage != null) {
            channel.setImage(channelImage);
        }
        // items
        channel.setItems(getItems(stagingPosts));

        return channel;
    }

    private void setChannelRequiredProperties(Channel channel, QueueDefinition queueDefinition) {
        // queue title defaults to queue ident if not specified
        String queueTitle = defaultString(queueDefinition.getTitle(), queueDefinition.getIdent());
        channel.setTitle(queueTitle);
        // TODO: should be 'the URL of the HTML website corresponding to the channel'
        channel.setLink(String.format(configProps.getChannelLinkTemplate(), queueDefinition.getTransportIdent()));
        // queue description defaults to queue title if not specified
        String queueDescription = defaultString(queueDefinition.getDescription(), queueTitle);
        channel.setDescription(queueDescription);
        channel.setTtl(configProps.getChannelTtl());
        channel.setLanguage(queueDefinition.getLanguage());
        channel.setCopyright(queueDefinition.getCopyright());
        channel.setGenerator(defaultString(queueDefinition.getGenerator(), configProps.getDefaultGeneratorValue()));
    }

    private static void setChannelOptionalProperties(Channel channel, JsonObject rssConfigObj) {
        if (rssConfigObj != null) {
            channel.setManagingEditor(getManagingEditor(rssConfigObj));
            channel.setWebMaster(getWebMaster(rssConfigObj));
            channel.setDocs(getDocs(rssConfigObj));
            channel.setCloud(getCloud(rssConfigObj));
            channel.setRating(getRating(rssConfigObj));
            channel.setTextInput(getTextInput(rssConfigObj));
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
        Image image = null;
        String queueImgTransport = queueDefinition.getQueueImgTransportIdent();
        if (isNotBlank(queueImgTransport)) {
            image = new Image();
            image.setUrl(String.format(configProps.getChannelImageUrlTemplate(), queueDefinition.getQueueImgTransportIdent())); // URL of the image
            image.setLink(String.format(configProps.getChannelLinkTemplate(), queueDefinition.getTransportIdent())); // URL of the channel
            image.setTitle(defaultString(queueDefinition.getTitle(), queueDefinition.getIdent()));
            image.setDescription(queueDefinition.getDescription());
            image.setHeight(configProps.getChannelImageHeight()); // height of the thumbnail we serve
            image.setWidth(configProps.getChannelImageWidth()); // width of the thumbnail we serve
        }
        return image;
    }

    //
    //
    //

    private static String getStringProperty(JsonObject obj, String propertyName) {
        if (obj != null) {
            if (obj.has(propertyName)) return obj.get(propertyName).getAsString();
        }
        return null;
    }

    private static Integer getIntegerProperty(JsonObject obj, @SuppressWarnings("SameParameterValue") String propertyName) {
        if (obj != null) {
            if (obj.has(propertyName)) return obj.get(propertyName).getAsNumber().intValue();
        }
        return null;
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

    private static TextInput getTextInput(JsonObject rssConfigObj) {
        String textInputDescription = getStringProperty(rssConfigObj, "textInputDescription");
        String textInputLink = getStringProperty(rssConfigObj, "textInputLink");
        String textInputTitle = getStringProperty(rssConfigObj, "textInputTitle");
        String textInputName = getStringProperty(rssConfigObj, "textInputName");
        if (isNotBlank(textInputDescription) || isNotBlank(textInputLink) || isNotBlank(textInputTitle) || isNotBlank(textInputName)) {
            TextInput textInput = new TextInput();
            textInput.setDescription(textInputDescription);
            textInput.setLink(textInputLink);
            textInput.setTitle(textInputTitle);
            textInput.setName(textInputName);
            return textInput;
        }

        return null;
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

    private static List<Item> getItems(Collection<? extends StagingPost> stagingPosts) {
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

    @Override
    public final String toString() {
        return "RSSChannelBuilder{" +
                "configProps=" + configProps +
                '}';
    }
}
