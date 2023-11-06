package com.lostsidewalk.buffy.rss;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lostsidewalk.buffy.post.StagingPost;
import com.lostsidewalk.buffy.queue.QueueDefinition;
import com.rometools.rome.feed.atom.*;
import com.rometools.rome.feed.synd.SyndPerson;
import com.rometools.rome.feed.synd.SyndPersonImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.CollectionUtils.size;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isNotBlank;


@Slf4j
class ATOMFeedBuilder {

    private static final Gson GSON = new Gson();

    private final RSSPublisherConfigProps configProps;

    ATOMFeedBuilder(RSSPublisherConfigProps configProps) {
        this.configProps = configProps;
    }

    //
    // FEED DEFINITION
    //

    final Feed buildFeed(QueueDefinition queueDefinition, Collection<? extends StagingPost> stagingPosts, Date pubDate) {
        Feed feed = new Feed();
        // feed type
        feed.setFeedType(configProps.getAtomFeedType()); // ok
        // other links
        feed.setOtherLinks(getOtherLinks(queueDefinition)); // ok
        // updated
        // last build date
        Date lastBuildDate = stagingPosts.stream()
                .filter(stagingPost -> stagingPost.getLastUpdatedTimestamp() != null)
                .max(comparing(StagingPost::getLastUpdatedTimestamp))
                .map(StagingPost::getLastUpdatedTimestamp)
                .orElse(null);
        feed.setUpdated(lastBuildDate); // ok
        // required
        setFeedRequiredProperties(feed, queueDefinition);
        // optional
        setFeedOptionalProperties(feed, getAtomConfigObj(queueDefinition));
        // entries
        List<Entry> entries = getEntries(stagingPosts, pubDate);
        if (isNotEmpty(entries)) {
            entries.forEach(e -> e.setSource(feed));
            feed.setEntries(entries);
        }

        return feed;
    }

    private List<Link> getOtherLinks(QueueDefinition queueDefinition) {
        Link link = new Link();
        link.setRel("self");
        link.setHref(String.format(configProps.getChannelUriTemplate(), queueDefinition.getTransportIdent()));
        return singletonList(link);
    }

    private void setFeedRequiredProperties(Feed feed, QueueDefinition queueDefinition) {
        feed.setTitle(defaultString(queueDefinition.getTitle(), queueDefinition.getIdent())); // ok
        feed.setSubtitle(getDescription(queueDefinition)); // ok
//        feed.setTagline(getDescription(queueDefinition)); // legacy
        feed.setId(String.format(configProps.getChannelUriTemplate(), queueDefinition.getTransportIdent())); // ok
        feed.setLanguage(queueDefinition.getLanguage()); // legacy
//        feed.setCopyright(queueDefinition.getCopyright()); // legacy
        feed.setRights(queueDefinition.getCopyright()); // ok
        feed.setGenerator(getGenerator(queueDefinition)); // ok
        //        feed.setModified(queueDefinition.getLastDeployed()); // legacy
        String queueImgTransportIdent = queueDefinition.getQueueImgTransportIdent();
        if (isNotBlank(queueImgTransportIdent)) {
            feed.setLogo(String.format(configProps.getChannelImageUrlTemplate(), queueImgTransportIdent)); // ok
            feed.setIcon(String.format(configProps.getChannelImageUrlTemplate(), queueImgTransportIdent)); // ok
        }
    }

    private Generator getGenerator(QueueDefinition queueDefinition) {
        Generator generator = new Generator();
        String generatorStr = queueDefinition.getGenerator();
        if (isNotBlank(generatorStr)) {
            generator.setValue(generatorStr);
//            generator.setUrl(EMPTY); // TODO: generator URL
//            generator.setVersion(EMPTY); // generation version
        } else {
            generator.setValue(configProps.getDefaultGeneratorValue());
            generator.setUrl(configProps.getDefaultGeneratorUrl());
            generator.setVersion(configProps.getDefaultGeneratorVersion());
        }
        return generator;
    }

    private static void setFeedOptionalProperties(Feed feed, JsonObject atomConfigObj) {
        if (atomConfigObj != null) {
            feed.setAuthors(getAuthors(atomConfigObj)); // ok
            feed.setContributors(getContributors(atomConfigObj)); // ok
//            feed.setInfo(getInfo(atomConfigObj)); // legacy
            feed.setCategories(getCategories(atomConfigObj)); // ok
        }
    }

    //
    //
    //

    private static JsonObject getAtomConfigObj(QueueDefinition queueDefinition) {
        JsonObject exportConfigObj = Optional.ofNullable(queueDefinition.getExportConfig())
                .map(Object::toString)
                .map(s -> GSON.fromJson(s, JsonObject.class))
                .orElse(null);
        return (exportConfigObj != null && exportConfigObj.has("atomConfig")) ?
                exportConfigObj.get("atomConfig").getAsJsonObject() :
                null;
    }

    private static Content getDescription(QueueDefinition queueDefinition) {
        Content subtitle = new Content();
        String description = queueDefinition.getDescription();
        if (isNotBlank(description)) {
            subtitle.setType("html");
            subtitle.setValue(queueDefinition.getDescription());
        } else {
            subtitle.setType("text");
            subtitle.setValue(queueDefinition.getIdent());
        }
        return subtitle;
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

    private static List<SyndPerson> getAuthors(JsonObject atomConfigObj) {
        String authorName = getStringProperty(atomConfigObj, "authorName");
        if (isNotBlank(authorName)) {
            SyndPerson author = new SyndPersonImpl();
            author.setName(authorName);
            author.setEmail(getStringProperty(atomConfigObj, "authorEmail"));
            author.setUri(getStringProperty(atomConfigObj, "authorUri"));
            return singletonList(author);
        }

        return null;
    }

    private static List<SyndPerson> getContributors(JsonObject atomConfigObj) {
        String contributorName = getStringProperty(atomConfigObj, "contributorName");
        if (isNotBlank(contributorName)) {
            SyndPerson contributor = new SyndPersonImpl();
            contributor.setName(contributorName);
            contributor.setEmail(getStringProperty(atomConfigObj, "contributorEmail"));
            contributor.setUri(getStringProperty(atomConfigObj, "contributorUri"));
            return singletonList(contributor);
        }

        return null;
    }

    private static List<Category> getCategories(JsonObject atomConfigObj) {
        String categoryTerm = getStringProperty(atomConfigObj, "categoryTerm");
        if (isNotBlank(categoryTerm)) {
            Category category = new Category();
            category.setTerm(categoryTerm);
            category.setLabel(getStringProperty(atomConfigObj, "categoryLabel"));
            category.setScheme(getStringProperty(atomConfigObj, "categoryScheme"));
            return singletonList(category);
        }

        return null;
    }

    //
    //
    //

    private static List<Entry> getEntries(Collection<? extends StagingPost> stagingPosts, Date pubDate) {
        List<Entry> entries = null;
        if (isNotEmpty(stagingPosts)) {
            entries = new ArrayList<>(size(stagingPosts));
            for (StagingPost stagingPost : stagingPosts) {
                Entry entry = ATOMFeedEntryBuilder.toEntry(stagingPost, pubDate);
                entries.add(entry);
            }
        }

        return entries;
    }

    @Override
    public final String toString() {
        return "ATOMFeedBuilder{" +
                "configProps=" + configProps +
                '}';
    }
}
