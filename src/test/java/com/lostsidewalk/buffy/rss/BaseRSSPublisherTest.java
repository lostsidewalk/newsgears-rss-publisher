package com.lostsidewalk.buffy.rss;

import com.lostsidewalk.buffy.RenderedFeedDao;
import com.lostsidewalk.buffy.feed.FeedDefinition;
import com.lostsidewalk.buffy.feed.FeedDefinitionDao;
import com.lostsidewalk.buffy.post.StagingPost;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {TestConfig.class})
@ContextConfiguration(classes = {RSSPublisher.class, RSSPublisherConfigProps.class})
class BaseRSSPublisherTest {

    @MockBean
    FeedDefinitionDao feedDefinitionDao;

    @MockBean
    RenderedFeedDao renderedFeedDao;

    @Autowired
    RSSPublisher rssPublisher;

    protected static final FeedDefinition TEST_FEED_DEFINITION = FeedDefinition.from(
            "testFeedIdent",
            "testTitle",
            "testDescription",
            "testGenerator",
            "testTransportIdent",
            "me",
            true, // is active
            "{ \"rssConfig\": { " +
                    "\"managingEditor\" : \"testManagingEditor\", " +
                    "\"webMaster\" : \"testWebMaster\", " +
                    "\"docs\" : \"testDocs\", " +
                    "\"cloudDomain\" : \"testCloudDomain\", " +
                    "\"cloudPath\" : \"testCloudPath\", " +
                    "\"cloudProtocol\" : \"testCloudProtocol\", " +
                    "\"cloudRegisterProcedure\" : \"testCloudRegisterProcedure\", " +
                    "\"categoryValue\" : \"testCategoryValue\", " +
                    "\"categoryDomain\" : \"testCategoryDomain\", " +
                    "\"rating\" : \"testRating\", " +
                    "\"skipHours\" : \"1,2\", " +
                    "\"skipDays\" : \"Monday,Tuesday\" " +
                "}, " +
            " \"atomConfig\": { " +
                    "\"authorName\" : \"testAuthorName\", " +
                    "\"authorEmail\" : \"testAuthorEmail\", " +
                    "\"authorUri\" : \"testAuthorUri\", " +
                    "\"contributorName\" : \"testContributorName\", " +
                    "\"contributorEmail\" : \"testContributorEmail\", " +
                    "\"contributorUri\" : \"testContributorUri\", " +
                    "\"categoryTerm\" : \"testCategoryTerm\", " +
                    "\"categoryLabel\" : \"testCategoryLabel\", " +
                    "\"categoryScheme\" : \"testCategoryScheme\", " +
                    "\"feedInfo\" : \"testFeedInfo\", " +
                    "\"xmlBase\" : \"testXmlBase\"" +
                "}" +
            "}",
            "testCopyright",
            "testLanguage",
            "testFeedImgSrc"
    );

    protected static final Date TEST_IMPORT_TIMESTAMP = new Date();

    protected static final Date TEST_EXPIRATION_TIMESTAMP = new Date();

    protected static final Date TEST_PUBLISH_TIMESTAMP = new Date();

    protected static final Date TEST_LAST_UPDATED_TIMESTAMP = new Date();

    protected static final StagingPost TEST_STAGING_POST = StagingPost.from(
            "testImporterId",
            "testFeedIdent",
            "testImporterDesc",
            "{}",
            "testSourceName",
            "testSourceUrl",
            "testPostTitle",
            "testPostDescription",
            "testPostUrl",
            "testPostImgUrl",
            TEST_IMPORT_TIMESTAMP, // import timestamp
            "testPostHash",
            "me",
            "testPostComment",
            true, // is published
            "testPostRights",
            "testXmlBase",
            "testContributorName",
            "testContributorEmail",
            "testAuthorName",
            "testAuthorEmail",
            "testPostCategory",
            TEST_PUBLISH_TIMESTAMP, // publish timestamp
            TEST_EXPIRATION_TIMESTAMP, // expiration timestamp
            "testEnclosureUrl",
            TEST_LAST_UPDATED_TIMESTAMP // last updated timestamp
    );
}
