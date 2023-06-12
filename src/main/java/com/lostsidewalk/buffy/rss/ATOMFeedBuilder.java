package com.lostsidewalk.buffy.rss;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lostsidewalk.buffy.post.StagingPost;
import com.lostsidewalk.buffy.queue.QueueDefinition;
import com.rometools.rome.feed.atom.*;
import com.rometools.rome.feed.synd.SyndPerson;
import com.rometools.rome.feed.synd.SyndPersonImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.CollectionUtils.size;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

class ATOMFeedBuilder {

    private static final Gson GSON = new Gson();

    private final RSSPublisherConfigProps configProps;

    ATOMFeedBuilder(RSSPublisherConfigProps configProps) {
        this.configProps = configProps;
    }

    //
    // FEED DEFINITION
    //

    Feed buildFeed(QueueDefinition queueDefinition, List<StagingPost> stagingPosts, Date pubDate) {
        Feed feed = new Feed();
        // feed type
        feed.setFeedType(this.configProps.getAtomFeedType()); // ok
        // other links
        feed.setOtherLinks(getOtherLinks(queueDefinition)); // ok
        // updated
        // last build date
        Date lastBuildDate = stagingPosts.stream()
                .filter(s -> s.getLastUpdatedTimestamp() != null)
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
        feed.setTitle(queueDefinition.getTitle()); // ok
        feed.setSubtitle(getDescription(queueDefinition)); // ok
//        feed.setTagline(getDescription(queueDefinition)); // legacy
        feed.setId(String.format(configProps.getChannelUriTemplate(), queueDefinition.getTransportIdent())); // ok
        feed.setLanguage(queueDefinition.getLanguage()); // ok
//        feed.setCopyright(queueDefinition.getCopyright()); // legacy
        feed.setRights(queueDefinition.getCopyright()); // ok
        feed.setGenerator(getGenerator(queueDefinition)); // ok
//        feed.setModified(queueDefinition.getLastDeployed()); // legacy
        feed.setLogo(String.format(configProps.getChannelImageUrlTemplate(), queueDefinition.getTransportIdent())); // ok
        feed.setIcon(String.format(configProps.getChannelImageUrlTemplate(), queueDefinition.getTransportIdent())); // ok
    }

    private void setFeedOptionalProperties(Feed feed, JsonObject atomConfigObj) {
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
        subtitle.setType("html");
        subtitle.setValue(queueDefinition.getDescription());
        return subtitle;
    }

    private static Generator getGenerator(QueueDefinition queueDefinition) {
        Generator generator = new Generator();
        String f = queueDefinition.getGenerator();
        generator.setValue(f);
//        generator.setUrl(EMPTY);
//        generator.setVersion(EMPTY);
        return generator;
    }

    //
    //
    //

    private static String getStringProperty(JsonObject obj, String propertyName) {
        return obj == null ? null : obj.has(propertyName) ? obj.get(propertyName).getAsString() : null;
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

    private static List<Entry> getEntries(List<StagingPost> stagingPosts, Date pubDate) {
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
}
