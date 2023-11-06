package com.lostsidewalk.buffy.rss;

import com.lostsidewalk.buffy.post.*;
import com.rometools.modules.itunes.ITunes;
import com.rometools.modules.mediarss.MediaEntryModule;
import com.rometools.rome.feed.atom.Category;
import com.rometools.rome.feed.atom.Content;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Link;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.synd.SyndPerson;
import com.rometools.rome.feed.synd.SyndPersonImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.CollectionUtils.size;
import static org.apache.commons.lang3.StringUtils.isNotBlank;


@Slf4j
class ATOMFeedEntryBuilder {

    static Entry toEntry(StagingPost stagingPost, Date pubDate) {
        Entry entry = new Entry();
        setEntryRequiredProperties(entry, stagingPost);
        setEntryOptionalProperties(entry, stagingPost, pubDate);
        return entry;
    }

    private static void setEntryRequiredProperties(Entry entry, StagingPost stagingPost) {
        entry.setId(stagingPost.getPostUrl()); // TODO: this is required, but post URL can be null
        entry.setTitleEx(getTitleEx(stagingPost));
        entry.setUpdated(stagingPost.getLastUpdatedTimestamp());
        entry.setSummary(getSummary(stagingPost));
    }

    private static Content getTitleEx(StagingPost stagingPost) {
        ContentObject postTitle = stagingPost.getPostTitle();
        if (postTitle != null) {
            Content title = new Content();
            title.setType(postTitle.getType());
            title.setValue(postTitle.getValue());
            return title;
        }

        return null;
    }

    private static Content getSummary(StagingPost stagingPost) {
        ContentObject postDesc = stagingPost.getPostDesc();
        if (postDesc != null) {
            Content summary = new Content();
            summary.setType(postDesc.getType());
            summary.setValue(postDesc.getValue());
            return summary;
        }

        return null;
    }

    private static void setEntryOptionalProperties(Entry entry, StagingPost stagingPost, Date pubDate) {
        entry.setAlternateLinks(getAlternateLinks(stagingPost));
        //
        List<Link> otherLinks = new ArrayList<>(16);
        otherLinks.addAll(getPostUrlLinks(stagingPost));
        otherLinks.addAll(getEnclosureLinks(stagingPost));
        entry.setOtherLinks(otherLinks);
        //
        entry.setAuthors(getAuthors(stagingPost));
        entry.setContributors(getContributors(stagingPost));
        entry.setRights(stagingPost.getPostRights());
//        entry.setCreated(stagingPost.getImportTimestamp()); // legacy
//        entry.setIssued(now); // legacy
        entry.setPublished(stagingPost.getPublishTimestamp() == null ? pubDate : stagingPost.getPublishTimestamp());
        entry.setCategories(getCategories(stagingPost));
        entry.setContents(getContents(stagingPost));
        entry.setModules(getModules(stagingPost));
    }

    private static List<Link> getAlternateLinks(StagingPost stagingPost) {
        List<Link> links = null;
        String postUrl = stagingPost.getPostUrl();
        if (isNotBlank(postUrl)) {
            Link link = new Link();
            link.setHref(stagingPost.getPostUrl());
            link.setRel("alternate");
            links = singletonList(link);
        }
        return links;
    }

    private static List<Link> getPostUrlLinks(StagingPost stagingPost) {
        // Note: skip the link w/rel=alternate (accounted for above)
        List<PostUrl> postUrls = stagingPost.getPostUrls();
        List<Link> links = new ArrayList<>(size(postUrls));
        if (isNotEmpty(postUrls)) {
            for (PostUrl postUrl : postUrls) {
                Link link = new Link();
                link.setTitle(postUrl.getTitle());
                link.setType(postUrl.getType());
                link.setHref(postUrl.getHref());
                link.setHreflang(postUrl.getHreflang());
                link.setRel(postUrl.getRel());
                links.add(link);
            }
        }

        return links;
    }

    private static List<Link> getEnclosureLinks(StagingPost stagingPost) {
        //
        List<PostEnclosure> postEnclosures = stagingPost.getEnclosures();
        List<Link> links = new ArrayList<>(size(postEnclosures));
        if (isNotEmpty(postEnclosures)) {
            for (PostEnclosure postEnclosure : postEnclosures) {
                Link link = new Link();
                link.setType(postEnclosure.getType());
                link.setLength(postEnclosure.getLength());
                link.setHref(postEnclosure.getUrl());
                link.setRel("enclosure");
                links.add(link);
            }
        }

        return links;
    }

    private static List<SyndPerson> getAuthors(StagingPost stagingPost) {
        List<PostPerson> postAuthors = stagingPost.getAuthors();
        List<SyndPerson> authors = null;
        if (isNotEmpty(postAuthors)) {
            authors = new ArrayList<>(size(postAuthors));
            for (PostPerson postAuthor : postAuthors) {
                SyndPerson author = new SyndPersonImpl();
                author.setName(postAuthor.getName());
                author.setEmail(postAuthor.getEmail());
                author.setUri(postAuthor.getUri());
                authors.add(author);
            }
        }

        return authors;
    }

    private static List<SyndPerson> getContributors(StagingPost stagingPost) {
        List<PostPerson> postContributors = stagingPost.getContributors();
        List<SyndPerson> contributors = null;
        if (isNotEmpty(postContributors)) {
            contributors = new ArrayList<>(size(postContributors));
            for (PostPerson postContributor : postContributors) {
                SyndPerson contributor = new SyndPersonImpl();
                contributor.setName(postContributor.getName());
                contributor.setEmail(postContributor.getEmail());
                contributor.setUri(postContributor.getUri());
                contributors.add(contributor);
            }
        }

        return contributors;
    }

    private static List<Category> getCategories(StagingPost stagingPost) {
        List<String> postCategories = stagingPost.getPostCategories();
        List<Category> categories = null;
        if (isNotEmpty(postCategories)) {
            categories = new ArrayList<>(size(postCategories));
            for (String postCategory : postCategories) {
                Category category = new Category();
                category.setTerm(postCategory);
                category.setLabel(postCategory);
                categories.add(category);
            }
        }

        return categories;
    }

    private static List<Content> getContents(StagingPost stagingPost) {
        List<Content> contents = null;
        List<ContentObject> postContents = stagingPost.getPostContents();
        if (isNotEmpty(postContents)) {
            contents = new ArrayList<>(size(postContents));
            for (ContentObject p : postContents) {
                Content content = new Content();
                content.setType(p.getType());
                content.setValue(p.getValue());
                contents.add(content);
            }
        }

        return contents;
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
