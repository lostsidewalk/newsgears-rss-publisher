package com.lostsidewalk.buffy.rss;


import com.lostsidewalk.buffy.DataAccessException;
import com.lostsidewalk.buffy.publisher.FeedPreview;
import com.lostsidewalk.buffy.publisher.Publisher;
import com.lostsidewalk.buffy.model.RenderedFeedDao;
import com.lostsidewalk.buffy.model.RenderedATOMFeed;
import com.lostsidewalk.buffy.model.RenderedRSSFeed;
import com.lostsidewalk.buffy.post.StagingPost;
import com.lostsidewalk.buffy.queue.QueueDefinition;
import com.lostsidewalk.buffy.queue.QueueDefinitionDao;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.WireFeedOutput;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import static com.lostsidewalk.buffy.publisher.Publisher.PubFormat.ATOM;
import static com.lostsidewalk.buffy.publisher.Publisher.PubFormat.RSS;
import static java.time.Instant.now;
import static org.apache.commons.collections4.CollectionUtils.size;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * The RSSPublisher class is responsible for publishing RSS and ATOM feeds based on staging posts.
 * It utilizes configured builders to generate feeds and stores them in Redis.
 * Additionally, it supports feed preview functionality and is capable of handling both RSS and ATOM formats.
 */
@Slf4j
@Component
public class RSSPublisher implements Publisher {

    @Autowired
    RSSPublisherConfigProps configProps;

    @Autowired
    RSSChannelBuilder rssChannelBuilder;

    @Autowired
    ATOMFeedBuilder atomFeedBuilder;

    @Autowired
    QueueDefinitionDao queueDefinitionDao;

    @Autowired
    RenderedFeedDao renderedFeedDao;

    /**
     * Default constructor; initializes the object.
     */
    RSSPublisher() {
    }

    /**
     * Initializes the RSSPublisher component after construction and logs the construction timestamp.
     */
    @PostConstruct
    protected static void postConstruct() {
        log.info("RSS publisher constructed at {}", now());
    }

    /**
     * Publishes RSS and ATOM feeds for a specified queue definition and staging posts.
     *
     * @param queueDefinition The queue definition for which feeds are generated.
     * @param stagingPosts    The staging posts to include in the feeds.
     * @param pubDate         The publication date for the feeds.
     * @return A map containing publication results for RSS and ATOM formats.
     */
    @Override
    public final Map<String, PubResult> publishFeed(QueueDefinition queueDefinition, List<StagingPost> stagingPosts, Date pubDate) {
        Map<String, PubResult> pubResults = new HashMap<>(2);
        String queueIdent = queueDefinition.getIdent();
        String transportIdent = queueDefinition.getTransportIdent();

        log.info("Deploying RSS/ATOM queueIdent={}", queueIdent);

        String rssTransportLinkUrl = null;
        String rssUserIdentLinkUrl = null;
        List<Throwable> rssErrors = new ArrayList<>(1);
        try {
            // build/publish the RSS feed
            Channel channel = rssChannelBuilder.buildChannel(queueDefinition, stagingPosts, pubDate);
            renderedFeedDao.putRSSFeedAtTransportIdent(transportIdent, RenderedRSSFeed.from(transportIdent, channel));
            rssTransportLinkUrl = String.format(configProps.getChannelLinkTemplate(), queueDefinition.getTransportIdent());
            rssUserIdentLinkUrl = String.format(configProps.getChannelLinkTemplate(), queueDefinition.getUsername() + "/" + queueIdent);
            log.info("Published RSS feed for queueIdent={}, transportIdent={}", queueIdent, transportIdent);
        } catch (DataAccessException | RuntimeException e) {
            rssErrors.add(e);
        }
        pubResults.put(RSS_PUBLISHER_ID, PubResult.from(rssTransportLinkUrl, rssUserIdentLinkUrl, rssErrors, pubDate));

        String atomTransportLinkUrl = null;
        String atomUserIdentLinkUrl = null;
        List<Throwable> atomErrors = new ArrayList<>(1);
        try {
            // build/publish the ATOM feed
            Feed feed = atomFeedBuilder.buildFeed(queueDefinition, stagingPosts, pubDate);
            RenderedATOMFeed renderedATOMFeed = RenderedATOMFeed.from(transportIdent, feed);
            renderedFeedDao.putATOMFeedAtTransportIdent(transportIdent, renderedATOMFeed);
            atomTransportLinkUrl = String.format(configProps.getChannelUriTemplate(), queueDefinition.getTransportIdent());
            atomUserIdentLinkUrl = String.format(configProps.getChannelUriTemplate(), queueDefinition.getUsername() + "/" + queueIdent);
            log.info("Published ATOM feed for queueIdent={}, transportIdent={}", queueIdent, transportIdent);
        } catch (DataAccessException | RuntimeException e) {
            atomErrors.add(e);
        }
        pubResults.put(ATOM_PUBLISHER_ID, PubResult.from(atomTransportLinkUrl, atomUserIdentLinkUrl, atomErrors, pubDate));

        return pubResults;
    }

    /**
     * Retrieves the publisher identifier for this RSSPublisher.
     *
     * @return The publisher identifier.
     */
    @Override
    public final String getPublisherId() {
        return RSS_PUBLISHER_ID;
    }

    /**
     * Checks if the publisher supports a given publication format (RSS or ATOM).
     *
     * @param pubFormat The publication format to check.
     * @return True if the publisher supports the format, false otherwise.
     */
    @Override
    public final boolean supportsFormat(PubFormat pubFormat) {
        return pubFormat == RSS || pubFormat == ATOM;
    }


    /**
     * Generates feed previews for a list of staging posts in the specified format.
     *
     * @param username       The username of the user.
     * @param incomingPosts  The list of staging posts to generate previews for.
     * @param format         The format of the feed previews (RSS or ATOM).
     * @return A list of feed preview artifacts.
     * @throws DataAccessException If an error occurs accessing data.
     */
    @Override
    public final List<FeedPreview> doPreview(String username, List<StagingPost> incomingPosts, PubFormat format) throws DataAccessException {
        log.info("RSS publisher has to {} posts to preview at {}", size(incomingPosts), now());
        // group posts by output file for tag
        Map<Long, List<StagingPost>> postsByFeedId = new HashMap<>(16);
        for (StagingPost incomingPost : incomingPosts) {
            postsByFeedId.computeIfAbsent(incomingPost.getQueueId(), t -> new ArrayList<>(size(incomingPosts))).add(incomingPost);
        }
        List<FeedPreview> feedPreviews = new ArrayList<>(postsByFeedId.keySet().size());
        for (Map.Entry<Long, List<StagingPost>> e : postsByFeedId.entrySet()) {
            FeedPreview feedPreview = previewFeed(username, e.getKey(), e.getValue(), format);
            if (feedPreview != null) {
                feedPreviews.add(feedPreview);
            }
        }
        log.info("RSS publisher preview finished at {}", now());
        return feedPreviews;
    }

    private FeedPreview previewFeed(String username, Long feedId, Collection<? extends StagingPost> stagingPosts, PubFormat format) throws DataAccessException {
        log.info("Previewing feed with id={}, format={}", (feedId == null ? "(all)" : feedId), format);
        String previewArtifact = EMPTY;
        QueueDefinition queueDefinition = queueDefinitionDao.findByQueueId(username, feedId);
        if (queueDefinition != null) {
            String transportIdent = queueDefinition.getTransportIdent();
            try {
                if (format == RSS) {
                    // preview the RSS feed
                    Channel channel = rssChannelBuilder.buildChannel(queueDefinition, stagingPosts, new Date());
                    log.info("Rendered RSS feed for feedId={}, transportIdent={}", feedId, transportIdent);
                    WireFeedOutput wireFeedOutput = new WireFeedOutput();
                    StringWriter channelWriter = new StringWriter();
                    wireFeedOutput.output(channel, channelWriter);
                    previewArtifact = channelWriter.toString();
                } else if (format == ATOM) {
                    // preview the ATOM feed
                    Feed feed = atomFeedBuilder.buildFeed(queueDefinition, stagingPosts, new Date());
                    log.info("Published ATOM feed for feedId={}, transportIdent={}", feedId, transportIdent);
                    WireFeedOutput wireFeedOutput = new WireFeedOutput();
                    StringWriter feedWriter = new StringWriter();
                    wireFeedOutput.output(feed, feedWriter);
                    previewArtifact = feedWriter.toString();
                }
            } catch (FeedException | IOException | IllegalArgumentException e) {
                log.error("Unable to rendered feed due to: {}", e.getMessage());
            }
        } else {
            log.warn("Unable to locate feed definition with Id={}", feedId);
        }

        //noinspection HardcodedLineSeparator
        previewArtifact = previewArtifact
                .replace("\n", EMPTY)
                .replace("\r", EMPTY);

        return FeedPreview.from(feedId, previewArtifact);
    }

    static final String RSS_PUBLISHER_ID = "RSS_20";

    static final String ATOM_PUBLISHER_ID = "ATOM_10";

    @Override
    public final String toString() {
        return "RSSPublisher{" +
                "configProps=" + configProps +
                ", rssChannelBuilder=" + rssChannelBuilder +
                ", atomFeedBuilder=" + atomFeedBuilder +
                ", queueDefinitionDao=" + queueDefinitionDao +
                ", renderedFeedDao=" + renderedFeedDao +
                '}';
    }
}
