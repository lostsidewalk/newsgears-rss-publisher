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
import com.rometools.rome.io.WireFeedOutput;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.*;

import static java.time.Instant.now;
import static org.apache.commons.collections4.CollectionUtils.size;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Slf4j
@Component
public class RSSPublisher implements Publisher {

    @Autowired
    RSSChannelBuilder rssChannelBuilder;

    @Autowired
    ATOMFeedBuilder atomFeedBuilder;

    @Autowired
    QueueDefinitionDao queueDefinitionDao;

    @Autowired
    RenderedFeedDao renderedFeedDao;

    @PostConstruct
    public void postConstruct() {
        log.info("RSS publisher constructed at {}", now());
    }

    @Override
    public PubResult publishFeed(QueueDefinition queueDefinition, List<StagingPost> stagingPosts, Date pubDate) {
        List<Throwable> errors = new ArrayList<>();
        String queueIdent = queueDefinition.getIdent();
        String transportIdent = queueDefinition.getTransportIdent();

        log.info("Deploying RSS/ATOM queueIdent={}", queueIdent);

        try {
            // build/publish the RSS feed
            Channel channel = this.rssChannelBuilder.buildChannel(queueDefinition, stagingPosts, pubDate);
            log.info("Published RSS feed for queueIdent={}, transportIdent={}", queueIdent, transportIdent);
            renderedFeedDao.putRSSFeedAtTransportIdent(transportIdent, RenderedRSSFeed.from(transportIdent, channel));
        } catch (Exception e) {
            errors.add(e);
        }

        try {
            // build/publish the ATOM feed
            Feed feed = this.atomFeedBuilder.buildFeed(queueDefinition, stagingPosts, pubDate);
            RenderedATOMFeed renderedATOMFeed = RenderedATOMFeed.from(transportIdent, feed);
            renderedFeedDao.putATOMFeedAtTransportIdent(transportIdent, renderedATOMFeed);
            log.info("Published ATOM feed for queueIdent={}, transportIdent={}", queueIdent, transportIdent);
        } catch (Exception e) {
            errors.add(e);
        }

        return PubResult.from(getPublisherId(), errors, pubDate);
    }

    @Override
    public String getPublisherId() {
        return RSS_PUBLISHER_ID;
    }

    @Override
    public boolean supportsFormat(PubFormat pubFormat) {
        return pubFormat == PubFormat.RSS || pubFormat == PubFormat.ATOM;
    }

    @Override
    public List<FeedPreview> doPreview(String username, List<StagingPost> incomingPosts, PubFormat format) throws Exception {
        log.info("RSS publisher has to {} posts to preview at {}", size(incomingPosts), now());
        // group posts by output file for tag
        Map<Long, List<StagingPost>> postsByFeedId = new HashMap<>();
        for (StagingPost incomingPost : incomingPosts) {
            postsByFeedId.computeIfAbsent(incomingPost.getQueueId(), t -> new ArrayList<>()).add(incomingPost);
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

    FeedPreview previewFeed(String username, Long feedId, List<StagingPost> stagingPosts, PubFormat format) throws DataAccessException {
        log.info("Previewing feed with id={}, format={}", (feedId == null ? "(all)" : feedId), format);
        String previewArtifact = EMPTY;
        QueueDefinition queueDefinition = this.queueDefinitionDao.findByQueueId(username, feedId);
        if (queueDefinition != null) {
            String transportIdent = queueDefinition.getTransportIdent();
            try {
                if (format == PubFormat.RSS) {
                    // preview the RSS feed
                    Channel channel = this.rssChannelBuilder.buildChannel(queueDefinition, stagingPosts, new Date());
                    log.info("Rendered RSS feed for feedId={}, transportIdent={}", feedId, transportIdent);
                    WireFeedOutput wireFeedOutput = new WireFeedOutput();
                    StringWriter channelWriter = new StringWriter();
                    wireFeedOutput.output(channel, channelWriter);
                    previewArtifact = channelWriter.toString();
                } else if (format == PubFormat.ATOM) {
                    // preview the ATOM feed
                    Feed feed = this.atomFeedBuilder.buildFeed(queueDefinition, stagingPosts, new Date());
                    log.info("Published ATOM feed for feedId={}, transportIdent={}", feedId, transportIdent);
                    WireFeedOutput wireFeedOutput = new WireFeedOutput();
                    StringWriter feedWriter = new StringWriter();
                    wireFeedOutput.output(feed, feedWriter);
                    previewArtifact = feedWriter.toString();
                }
            } catch (Exception e) {
                log.error("Unable to rendered feed due to: {}", e.getMessage());
            }
        } else {
            log.warn("Unable to locate feed definition with Id={}", feedId);
        }

        previewArtifact = previewArtifact
                .replace("\n", EMPTY)
                .replace("\r", EMPTY);

        return FeedPreview.from(feedId, previewArtifact);
    }

    private static final String RSS_PUBLISHER_ID = "RSS";
}
