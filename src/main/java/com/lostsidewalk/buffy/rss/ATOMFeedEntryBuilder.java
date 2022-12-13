package com.lostsidewalk.buffy.rss;

import com.lostsidewalk.buffy.post.StagingPost;
import com.rometools.rome.feed.atom.Category;
import com.rometools.rome.feed.atom.Content;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Link;
import com.rometools.rome.feed.synd.SyndPerson;
import com.rometools.rome.feed.synd.SyndPersonImpl;

import java.util.Date;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

class ATOMFeedEntryBuilder {

    static Entry toEntry(StagingPost stagingPost, Date pubDate) {
        Entry entry = new Entry();
        setEntryRequiredProperties(entry, stagingPost);
        setEntryOptionalProperties(entry, stagingPost, pubDate);
        return entry;
    }

    private static void setEntryRequiredProperties(Entry entry, StagingPost stagingPost) {
        entry.setId(stagingPost.getPostUrl()); // ok
        entry.setTitle(stagingPost.getPostTitle()); // ok
        entry.setUpdated(stagingPost.getLastUpdatedTimestamp()); // ok
        entry.setAlternateLinks(getAlternateLinks(stagingPost)); // ok
        entry.setSummary(getSummary(stagingPost)); // ok
    }

    private static List<Link> getAlternateLinks(StagingPost stagingPost) {
        Link link = new Link();
        link.setHref(stagingPost.getPostUrl());
        link.setRel("self");
        return singletonList(link);
    }

    private static Content getSummary(StagingPost stagingPost) {
        String postDesc = stagingPost.getPostDesc();

        if (isNotBlank(postDesc)) {
            Content summary = new Content();
            summary.setType("text/plain");
            summary.setValue(postDesc);
            return summary;
        }

        return null;
    }

    private static void setEntryOptionalProperties(Entry entry, StagingPost stagingPost, Date pubDate) {
        entry.setAuthors(getAuthors(stagingPost)); // ok
        entry.setContributors(getContributors(stagingPost)); // ok
        entry.setRights(stagingPost.getPostRights()); // ok
        entry.setXmlBase(stagingPost.getXmlBase());
//        entry.setCreated(stagingPost.getImportTimestamp()); // legacy
//        entry.setIssued(now); // legacy
        entry.setPublished(stagingPost.getPublishTimestamp() == null ? pubDate : stagingPost.getPublishTimestamp()); // ok
        entry.setCategories(getCategories(stagingPost)); // ok
    }

    private static List<SyndPerson> getAuthors(StagingPost stagingPost) {
        String authorName = stagingPost.getAuthorName();
        String authorEmail = stagingPost.getAuthorEmail();

        if (isNotBlank(authorName) || isNotBlank(authorEmail)) {
            SyndPerson author = new SyndPersonImpl();
            author.setName(authorName);
            author.setEmail(authorEmail);
//        author.setUri(stagingPost.getAuthorUri());
            return singletonList(author);
        }

        return null;
    }

    private static List<SyndPerson> getContributors(StagingPost stagingPost) {
        String contributorName = stagingPost.getContributorName();
        String contributorEmail = stagingPost.getContributorEmail();

        if (isNotBlank(contributorName) || isNotBlank(contributorEmail)) {
            SyndPerson contributor = new SyndPersonImpl();
            contributor.setName(contributorName);
            contributor.setEmail(contributorEmail);
//        contributor.setUri(stagingPost.getContributorUri());
            return singletonList(contributor);
        }

        return null;
    }

    private static List<Category> getCategories(StagingPost stagingPost) {
        String categoryTerm = stagingPost.getPostCategory();

        if (isNotBlank(categoryTerm)) {
            Category category = new Category();
            category.setTerm(categoryTerm);
            category.setLabel(categoryTerm);
//            category.setScheme(EMPTY);
            return singletonList(category);
        }

        return null;
    }
}
