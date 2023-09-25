package com.lostsidewalk.buffy.rss;

import com.lostsidewalk.buffy.post.*;
import com.rometools.modules.itunes.ITunes;
import com.rometools.modules.mediarss.MediaEntryModule;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.rss.*;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.CollectionUtils.size;

class RSSChannelItemBuilder {

    static Item toItem(StagingPost stagingPost) {
        Item item = new Item();
        setItemRequiredProperties(item, stagingPost);
        setItemOptionalProperties(item, stagingPost);
        return item;
    }

    private static void setItemRequiredProperties(Item item, StagingPost stagingPost) {
        item.setTitle(getTitle(stagingPost));
        item.setLink(stagingPost.getPostUrl());
        item.setUri(stagingPost.getPostUrl());
        item.setDescription(getDescription(stagingPost));
    }

    private static String getTitle(StagingPost stagingPost) {
        ContentObject postTitle = stagingPost.getPostTitle();
        if (postTitle != null) {
            return postTitle.getValue();
        }

        return null;
    }

    private static Description getDescription(StagingPost stagingPost) {
        ContentObject postDesc = stagingPost.getPostDesc();
        if (postDesc != null) {
            Description descr = new Description();
            descr.setValue(postDesc.getValue());
            descr.setType(postDesc.getType());

            return descr;
        }

        return null;
    }

    private static void setItemOptionalProperties(Item item, StagingPost stagingPost) {
        item.setAuthor(getAuthor(stagingPost));
        item.setCategories(getCategories(stagingPost));
        item.setComments(stagingPost.getPostComment());
        item.setEnclosures(getEnclosures(stagingPost));
        item.setGuid(getGuid(stagingPost));
        item.setPubDate(stagingPost.getPublishTimestamp());
        item.setExpirationDate(stagingPost.getExpirationTimestamp());
        item.setContent(getContent(stagingPost));
        item.setModules(getModules(stagingPost));
    }

    private static String getAuthor(StagingPost stagingPost) {
        List<PostPerson> authors = stagingPost.getAuthors();
        if (isNotEmpty(authors)) {
            PostPerson author = authors.get(0);
            return author.getName();
        }

        return null;
    }

    private static List<Category> getCategories(StagingPost stagingPost) {
        List<String> postCategories = stagingPost.getPostCategories();
        List<Category> categories = null;
        if (isNotEmpty(postCategories)) {
            categories = new ArrayList<>(size(postCategories));
            for (String postCategory : postCategories) {
                Category category = new Category();
                category.setValue(postCategory);
                categories.add(category);
            }
        }

        return categories;
    }

    private static List<Enclosure> getEnclosures(StagingPost stagingPost) {
        List<PostEnclosure> postEnclosures = stagingPost.getEnclosures();
        List<Enclosure> enclosures = null;
        if (isNotEmpty(postEnclosures)) {
            enclosures = new ArrayList<>(size(postEnclosures));
            for (PostEnclosure postEnclosure : postEnclosures) {
                Enclosure enclosure = new Enclosure();
                enclosure.setUrl(postEnclosure.getUrl());
                enclosure.setType(postEnclosure.getType());
                Long length = postEnclosure.getLength();
                if (length != null) {
                    enclosure.setLength(length);
                }
                enclosures.add(enclosure);
            }
        }

        return enclosures;
    }

    private static Guid getGuid(StagingPost stagingPost) {
        Guid guid = new Guid();
        guid.setValue(stagingPost.getPostHash());
        guid.setPermaLink(false);
        return guid;
    }

    private static Content getContent(StagingPost stagingPost) {
        Content content = null;
        List<ContentObject> postContents = stagingPost.getPostContents();
        if (isNotEmpty(postContents)) {
            ContentObject contentObject = postContents.get(0);
            content = new Content();
            content.setType(contentObject.getType());
            content.setValue(contentObject.getValue());
        }

        return content;
    }

    private static List<Module> getModules(StagingPost stagingPost) {
        List<Module> modules = new ArrayList<>(2);
        // post media
        PostMedia postMedia = stagingPost.getPostMedia();
        if (postMedia != null) {
            MediaEntryModule mm = postMedia.toModule();
            modules.add(mm);
        }
        PostITunes postITunes = stagingPost.getPostITunes();
        // post iTunes
        if (postITunes != null) {
            ITunes im = postITunes.toEntryModule();
            modules.add(im);
        }

        return modules.isEmpty() ? null : modules;
    }
}
