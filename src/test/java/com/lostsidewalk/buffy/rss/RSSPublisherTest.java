package com.lostsidewalk.buffy.rss;

import com.lostsidewalk.buffy.publisher.Publisher;
import com.lostsidewalk.buffy.model.RenderedRSSFeed;
import com.rometools.rome.feed.rss.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

class RSSPublisherTest extends BaseRSSPublisherTest {

    @Test
    public void testRssPublisher_RSS() {
        // setup mocks
        ArgumentCaptor<RenderedRSSFeed> rssChannelValueCapture = ArgumentCaptor.forClass(RenderedRSSFeed.class);
        try {
            doNothing().when(this.renderedFeedDao).putRSSFeedAtTransportIdent(anyString(), rssChannelValueCapture.capture());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        // invoke test
        Publisher.PubResult pubResult = rssPublisher.publishFeed(TEST_QUEUE_DEFINITION, singletonList(TEST_STAGING_POST), TEST_PUBLISH_TIMESTAMP);
        // evaluate the result
        assertNotNull(pubResult);
        RenderedRSSFeed renderedRSSFeed = rssChannelValueCapture.getValue();
        assertNotNull(renderedRSSFeed);
        assertEquals("testTransportIdent", renderedRSSFeed.getTransportIdent());
        Channel channel = renderedRSSFeed.getChannel();
        assertNotNull(channel);
        // validate channel required properties
        validateChannelRequiredProperties(channel);
        // validate channel optional properties
        validateChannelOptionalProperties(channel);
        // validate channel items
        validateChannelItems(channel);
    }

    private static void validateChannelRequiredProperties(Channel channel) {
        // title
        assertEquals("testTitle", channel.getTitle());
        // link
        assertEquals("https://localhost/rss/testTransportIdent", channel.getLink());
        // description
        assertEquals("testDescription", channel.getDescription());
        // ttl
        assertEquals(10, channel.getTtl());
        // language
        assertEquals("testLanguage", channel.getLanguage());
        // copyright
        assertEquals("testCopyright", channel.getCopyright());
        // generator
        assertEquals("testGenerator", channel.getGenerator());
        // pub date
        assertNotNull(channel.getPubDate());
        // last build date
        assertNotNull(channel.getLastBuildDate());
        // categories
        List<Category> categories = channel.getCategories();
        assertNotNull(categories);
        assertEquals(categories.size(), 1);
        Category category = categories.get(0);
        // (category domain)
        assertEquals("testCategoryDomain", category.getDomain());
        // (category value)
        assertEquals("testCategoryValue", category.getValue());
        // image
        Image image = channel.getImage();
        assertNotNull(image);
        // (image title)
        assertEquals("testTitle", image.getTitle());
        // (image description)
        assertEquals("testDescription", image.getDescription());
        // (image link)
        assertEquals("https://localhost/rss/testTransportIdent", image.getLink());
        // (image url)
        assertEquals("https://localhost/rss/testTransportIdent", image.getUrl());
    }

    private static void validateChannelOptionalProperties(Channel channel) {
        assertEquals("testManagingEditor", channel.getManagingEditor());
        assertEquals("testWebMaster", channel.getWebMaster());
        assertEquals("testDocs", channel.getDocs());
        Cloud cloud = channel.getCloud();
        assertNotNull(cloud);
        assertEquals("testCloudDomain", cloud.getDomain());
        assertEquals("testCloudPath", cloud.getPath());
        assertEquals("testCloudProtocol", cloud.getProtocol());
        assertEquals("testCloudRegisterProcedure", cloud.getRegisterProcedure());
        assertEquals("testRating", channel.getRating());
        assertEquals(List.of(1,2), channel.getSkipHours());
        assertEquals(List.of("monday", "tuesday"), channel.getSkipDays());
    }

    private static void validateChannelItems(Channel channel) {
        List<Item> items = channel.getItems();
        assertNotNull(items);
        assertEquals(1, items.size());
        Item item = items.get(0);
        //
        validateItemRequiredProperties(item);
        //
        validateItemOptionalProperties(item);
    }

    private static void validateItemRequiredProperties(Item item) {
        assertEquals("testPostTitle", item.getTitle());
        assertEquals("testPostUrl", item.getLink());
        assertEquals("testPostUrl", item.getUri());
        Description description = item.getDescription();
        assertNotNull(description);
        assertEquals("testPostDescription", description.getValue());
    }

    private static void validateItemOptionalProperties(Item item) {
        assertEquals("testAuthorName", item.getAuthor());
        List<Category> itemCategories = item.getCategories();
        assertNotNull(itemCategories);
        assertEquals(1, itemCategories.size());
        Category itemCategory = itemCategories.get(0);
        assertEquals("testPostCategory", itemCategory.getValue());
        assertEquals("testPostComment", item.getComments());
        List<Enclosure> enclosures = item.getEnclosures();
        assertNotNull(enclosures);
        assertEquals(1, enclosures.size());
        Enclosure enclosure = enclosures.get(0);
        assertEquals("testEnclosureUrl", enclosure.getUrl());
        Guid guid = item.getGuid();
        assertNotNull(guid);
        assertEquals("testPostUrl", guid.getValue());
        assertEquals(TEST_PUBLISH_TIMESTAMP, item.getPubDate());
        assertNull(item.getSource());
    }
}
