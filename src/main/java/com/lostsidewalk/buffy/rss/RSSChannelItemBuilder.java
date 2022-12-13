package com.lostsidewalk.buffy.rss;

import com.lostsidewalk.buffy.post.StagingPost;
import com.rometools.rome.feed.rss.*;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

class RSSChannelItemBuilder {

    static Item toItem(StagingPost stagingPost) {
        Item item = new Item();
        setItemRequiredProperties(item, stagingPost);
        setItemOptionalProperties(item, stagingPost);
        return item;
    }

    private static void setItemRequiredProperties(Item item, StagingPost stagingPost) {
        item.setTitle(stagingPost.getPostTitle());
        item.setLink(stagingPost.getPostUrl());
        item.setUri(stagingPost.getPostUrl());
        item.setDescription(getDescription(stagingPost));
    }

    private static Description getDescription(StagingPost stagingPost) {
        String postDesc = stagingPost.getPostDesc();
        if (isNotBlank(postDesc)) {
            Description descr = new Description();
            descr.setValue(postDesc);
            descr.setType("text/plain");
            return descr;
        }

        return null;
    }

    private static void setItemOptionalProperties(Item item, StagingPost stagingPost) {
        item.setAuthor(stagingPost.getAuthorName());
        item.setCategories(getCategories(stagingPost));
        item.setComments(stagingPost.getPostComment());
        item.setEnclosures(getEnclosures(stagingPost));
        item.setGuid(getGuid(stagingPost));
        item.setPubDate(stagingPost.getPublishTimestamp());
        item.setSource(getSource(stagingPost));
        item.setExpirationDate(stagingPost.getExpirationTimestamp());
    }

    private static List<Category> getCategories(@SuppressWarnings("unused") StagingPost stagingPost) {
        String postCategory = stagingPost.getPostCategory();
        if (isNotBlank(postCategory)) {
            Category category = new Category();
            category.setValue(postCategory);
            return singletonList(category);
        }

        return null;
    }

    private static List<Enclosure> getEnclosures(StagingPost stagingPost) {
        String enclosureUrl = stagingPost.getEnclosureUrl();
        if (isNotBlank(enclosureUrl)) {
            Enclosure enclosure = new Enclosure();
            enclosure.setType(null); // TODO: this is the MIME type of the item at the enclosureUrl
//            enclosure.setLength(0L); // TODO: this is the length of the item at the enclosureUrl
            enclosure.setUrl(enclosureUrl);
            return singletonList(enclosure);
        }

        return null;
    }

    private static Guid getGuid(StagingPost stagingPost) {
        String postUrl = stagingPost.getPostUrl();
        if (postUrl != null) {
            Guid guid = new Guid();
            guid.setValue(postUrl);
            guid.setPermaLink(true);
            return guid;
        }

        return null;
    }

    private static Source getSource(StagingPost stagingPost) {
        String sourceName = stagingPost.getSourceName();
        String sourceUrl = stagingPost.getSourceUrl();
        if (isNotBlank(sourceName) || isNotBlank(sourceUrl)) {
            Source source = new Source();
            source.setValue(sourceName);
            source.setUrl(sourceUrl);
            return source;
        }

        return null;
    }
}
