package com.lostsidewalk.buffy.rss;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lostsidewalk.buffy.feed.FeedDefinition;
import com.lostsidewalk.buffy.post.StagingPost;
import com.rometools.rome.feed.atom.*;
import com.rometools.rome.feed.synd.SyndPerson;
import com.rometools.rome.feed.synd.SyndPersonImpl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
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

    Feed buildFeed(FeedDefinition feedDefinition, List<StagingPost> stagingPosts, Date pubDate) {
        Feed feed = new Feed();
        // feed type
        feed.setFeedType(this.configProps.getAtomFeedType()); // ok
        // other links
        feed.setOtherLinks(getOtherLinks(feedDefinition)); // ok
        // updated
        // last build date
        Date lastBuildDate = stagingPosts.stream()
                .filter(s -> s.getLastUpdatedTimestamp() != null)
                .max(comparing(StagingPost::getLastUpdatedTimestamp))
                .map(StagingPost::getLastUpdatedTimestamp)
                .orElse(null);
        feed.setUpdated(lastBuildDate); // ok
        // required
        setFeedRequiredProperties(feed, feedDefinition);
        // optional
        setFeedOptionalProperties(feed, getAtomConfigObj(feedDefinition));
        // entries
        List<Entry> entries = getEntries(stagingPosts, pubDate);
        entries.forEach(e -> e.setSource(feed));
        feed.setEntries(entries);

        return feed;
    }

    private List<Link> getOtherLinks(FeedDefinition feedDefinition) {
        Link link = new Link();
        link.setRel("self");
        link.setHref(String.format(configProps.getChannelUriTemplate(), feedDefinition.getTransportIdent()));
        return singletonList(link);
    }

    private void setFeedRequiredProperties(Feed feed, FeedDefinition feedDefinition) {
        feed.setTitle(feedDefinition.getTitle()); // ok
        feed.setSubtitle(getDescription(feedDefinition)); // ok
//        feed.setTagline(getDescription(feedDefinition)); // legacy
        feed.setId(String.format(configProps.getChannelUriTemplate(), feedDefinition.getTransportIdent())); // ok
        feed.setLanguage(feedDefinition.getLanguage()); // ok
//        feed.setCopyright(feedDefinition.getCopyright()); // legacy
        feed.setRights(feedDefinition.getCopyright()); // ok
        feed.setGenerator(getGenerator(feedDefinition)); // ok
//        feed.setModified(feedDefinition.getLastDeployed()); // legacy
        feed.setLogo(String.format(configProps.getChannelImageUrlTemplate(), feedDefinition.getTransportIdent())); // ok
        feed.setIcon(String.format(configProps.getChannelImageUrlTemplate(), feedDefinition.getTransportIdent())); // ok
    }

    private void setFeedOptionalProperties(Feed feed, JsonObject atomConfigObj) {
        if (atomConfigObj != null) {
            feed.setAuthors(getAuthors(atomConfigObj)); // ok
            feed.setContributors(getContributors(atomConfigObj)); // ok
//            feed.setInfo(getInfo(atomConfigObj)); // legacy
            feed.setCategories(getCategories(atomConfigObj)); // ok
            feed.setXmlBase(getXmlBase(atomConfigObj)); // ok
        }
    }

    //
    //
    //

    private static JsonObject getAtomConfigObj(FeedDefinition feedDefinition) {
        JsonObject exportConfigObj = Optional.ofNullable(feedDefinition.getExportConfig())
                .map(Object::toString)
                .map(s -> GSON.fromJson(s, JsonObject.class))
                .orElse(null);
        return (exportConfigObj != null && exportConfigObj.has("atomConfig")) ?
                exportConfigObj.get("atomConfig").getAsJsonObject() :
                null;
    }

    private static Content getDescription(FeedDefinition feedDefinition) {
        Content subtitle = new Content();
        subtitle.setType("text/plain");
        subtitle.setValue(feedDefinition.getDescription());
        return subtitle;
    }

    private static Generator getGenerator(FeedDefinition feedDefinition) {
        Generator generator = new Generator();
        String f = feedDefinition.getGenerator();
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

    private static String getXmlBase(JsonObject atomConfigObj) {
        return getStringProperty(atomConfigObj, "xmlBase");
    }

    //
    //
    //

    private static List<Entry> getEntries(List<StagingPost> stagingPosts, Date pubDate) {
        return stagingPosts.stream()
                .map(s -> ATOMFeedEntryBuilder.toEntry(s, pubDate))
                .collect(toList());
    }
}
