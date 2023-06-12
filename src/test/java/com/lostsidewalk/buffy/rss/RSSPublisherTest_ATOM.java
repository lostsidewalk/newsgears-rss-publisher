package com.lostsidewalk.buffy.rss;

import com.lostsidewalk.buffy.publisher.Publisher;
import com.lostsidewalk.buffy.model.RenderedATOMFeed;
import com.rometools.rome.feed.atom.*;
import com.rometools.rome.feed.synd.SyndPerson;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.apache.commons.collections4.CollectionUtils.size;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

class RSSPublisherTest_ATOM extends BaseRSSPublisherTest {

    @Test
    public void testRssPublisher_ATOM() {
        // setup mocks
        ArgumentCaptor<RenderedATOMFeed> atomFeedValueCapture = ArgumentCaptor.forClass(RenderedATOMFeed.class);
        try {
            doNothing().when(this.renderedFeedDao).putATOMFeedAtTransportIdent(anyString(), atomFeedValueCapture.capture());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        // invoke test
        Publisher.PubResult pubResult = rssPublisher.publishFeed(TEST_QUEUE_DEFINITION, singletonList(TEST_STAGING_POST), TEST_PUBLISH_TIMESTAMP);
        // evaluate the result
        assertNotNull(pubResult);
        assertEquals(0, size(pubResult.getErrors()));
        RenderedATOMFeed renderedATOMFeed = atomFeedValueCapture.getValue();
        assertNotNull(renderedATOMFeed);
        assertEquals("testTransportIdent", renderedATOMFeed.getTransportIdent());
        Feed feed = renderedATOMFeed.getFeed();
        assertNotNull(feed);
        // validate feed required properties
        validateFeedRequiredProperties(feed);
        // validate feed optional properties
        validateFeedOptionalProperties(feed);
        // validate feed entries
        validateFeedEntries(feed);
    }

    private static void validateFeedRequiredProperties(Feed feed) {
        //
        List<Link> otherLinks = feed.getOtherLinks();
        assertNotNull(otherLinks);
        assertEquals(1, otherLinks.size());
        Link link = otherLinks.get(0);
        assertEquals("self", link.getRel());
        assertEquals("https://localhost/rss/testTransportIdent", link.getHref());
        //
        assertEquals("testTitle", feed.getTitle());
        //
        com.rometools.rome.feed.atom.Content subtitle = feed.getSubtitle();
        assertNotNull(subtitle);
        assertEquals("html", subtitle.getType());
        assertEquals("testDescription", subtitle.getValue());
        //
        assertNotNull(feed.getId());
        //
        assertEquals("testLanguage", feed.getLanguage());
        //
        assertEquals("testCopyright", feed.getCopyright());
        //
        Generator generator = feed.getGenerator();
        assertNotNull(generator);
        assertEquals("testGenerator", generator.getValue());
        //
        assertNotNull(feed.getUpdated());
        //
        assertNotNull(feed.getModified());
        //
        List<com.rometools.rome.feed.atom.Category> categories = feed.getCategories();
        assertNotNull(categories);
        assertEquals(1, categories.size());
        com.rometools.rome.feed.atom.Category category = categories.get(0);
        assertEquals("testCategoryTerm", category.getTerm());
        assertEquals("testCategoryLabel", category.getLabel());
        assertEquals("testCategoryScheme", category.getScheme());
        assertEquals("testCategoryScheme", category.getSchemeResolved());
        //
        assertEquals("https://localhost/rss/testTransportIdent", feed.getLogo());
    }

    private static void validateFeedOptionalProperties(Feed feed) {
        List<SyndPerson> authors = feed.getAuthors();
        assertNotNull(authors);
        assertEquals(1, authors.size());
        SyndPerson author = authors.get(0);
        assertEquals("testAuthorName", author.getName());
        assertEquals("testAuthorEmail", author.getEmail());

        List<SyndPerson> contributors = feed.getContributors();
        assertNotNull(contributors);
        assertEquals(1, contributors.size());
        SyndPerson contributor = contributors.get(0);
        assertEquals("testContributorName", contributor.getName());
        assertEquals("testContributorEmail", contributor.getEmail());
    }

    private static void validateFeedEntries(Feed feed) {
        List<Entry> entries = feed.getEntries();
        assertNotNull(entries);
        assertEquals(1, entries.size());
        Entry entry = entries.get(0);
        //
        validateEntryRequiredProperties(entry);
        //
        validateEntryOptionalProperties(entry);
    }

    private static void validateEntryRequiredProperties(Entry entry) {
        // id
        String idStr = entry.getId();
        assertEquals("testPostUrl", idStr);
        // title
        assertEquals("testPostTitle", entry.getTitle());
        // alternate links
        List<Link> alternateLinks = entry.getAlternateLinks();
        assertNotNull(alternateLinks);
        assertEquals(1, alternateLinks.size());
        Link link = alternateLinks.get(0);
        assertEquals("testPostUrl", link.getHref());
        // summary
        Content summary = entry.getSummary();
        assertNotNull(summary);
        assertEquals("text", summary.getType());
        assertEquals("testPostDescription", summary.getValue());
    }

    private static void validateEntryOptionalProperties(Entry entry) {
        // contributors
        List<SyndPerson> contributors = entry.getContributors();
        assertNotNull(contributors);
        assertEquals(1, contributors.size());
        SyndPerson contributor = contributors.get(0);
        assertEquals("testContributorName", contributor.getName());
        assertEquals("testContributorEmail", contributor.getEmail());
//        assertEquals("testContributorUri", contributor.getUri());
        // rights
        assertEquals("testPostRights", entry.getRights());
        // xml base
        assertNull(entry.getXmlBase());
        // authors
        List<SyndPerson> authors = entry.getAuthors();
        assertNotNull(authors);
        assertEquals(1, authors.size());
        SyndPerson author = authors.get(0);
        assertEquals("testAuthorName", author.getName());
        assertEquals("testAuthorEmail", author.getEmail());
//        assertEquals("testAuthorUri", author.getUri());
//        assertEquals(TEST_IMPORT_TIMESTAMP, entry.getCreated());
        // issued
        assertNotNull(entry.getIssued());
        // published
        assertEquals(TEST_PUBLISH_TIMESTAMP, entry.getPublished());
        // updated
        assertNotNull(entry.getUpdated());
        // categories
        List<Category> categories = entry.getCategories();
        assertNotNull(categories);
        assertEquals(1, categories.size());
        Category category = categories.get(0);
        assertEquals("testPostCategory", category.getTerm());
    }
}
