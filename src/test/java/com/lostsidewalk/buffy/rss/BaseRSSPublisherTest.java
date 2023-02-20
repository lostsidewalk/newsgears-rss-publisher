package com.lostsidewalk.buffy.rss;

import com.lostsidewalk.buffy.RenderedFeedDao;
import com.lostsidewalk.buffy.feed.FeedDefinition;
import com.lostsidewalk.buffy.feed.FeedDefinitionDao;
import com.lostsidewalk.buffy.post.*;
import com.rometools.modules.itunes.EntryInformationImpl;
import com.rometools.modules.itunes.ITunes;
import com.rometools.modules.mediarss.MediaEntryModuleImpl;
import com.rometools.modules.mediarss.types.Metadata;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

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
                        "\"feedInfo\" : \"testFeedInfo\" " +
                    "}" +
                "}",
            "testCopyright",
            "testLanguage",
            "testFeedImgSrc"
    );
    static {
        TEST_FEED_DEFINITION.setId(666L);
    }

    protected static final Date TEST_IMPORT_TIMESTAMP = new Date();

    protected static final Date TEST_EXPIRATION_TIMESTAMP = new Date();

    protected static final Date TEST_PUBLISH_TIMESTAMP = new Date();

    protected static final Date TEST_LAST_UPDATED_TIMESTAMP = new Date();

    protected static final ContentObject TEST_POST_TITLE = ContentObject.from("text", "testPostTitle");

    protected static final ContentObject TEST_POST_DESCRIPTION = ContentObject.from("text", "testPostDescription");

    protected static final ContentObject TEST_POST_CONTENT = ContentObject.from("text", "testPostContent");

    protected static final PostMedia TEST_POST_MEDIA;
    static {
        MediaEntryModuleImpl testMediaEntryModule = new MediaEntryModuleImpl();
        Metadata metadata = new Metadata();
        testMediaEntryModule.setMetadata(metadata);
        TEST_POST_MEDIA = PostMedia.from(testMediaEntryModule);
    }

    protected static final PostITunes TEST_POST_ITUNES;
    static {
        ITunes testITunes = new EntryInformationImpl();
        TEST_POST_ITUNES = PostITunes.from(testITunes);
    }

    protected static final PostUrl TEST_POST_URL = new PostUrl();
    static {
        TEST_POST_URL.setTitle("testUrlTitle");
        TEST_POST_URL.setRel("testUrlRel");
        TEST_POST_URL.setHref("testUrlHref");
        TEST_POST_URL.setHreflang("testUrlHreflang");
        TEST_POST_URL.setType("testUrlType");
    }

    protected static final PostPerson TEST_POST_CONTRIBUTOR = new PostPerson();
    static {
        TEST_POST_CONTRIBUTOR.setName("testContributorName");
        TEST_POST_CONTRIBUTOR.setUri("testContributorUri");
        TEST_POST_CONTRIBUTOR.setEmail("testContributorEmail");
    }

    protected static final PostPerson TEST_POST_AUTHOR = new PostPerson();
    static {
        TEST_POST_AUTHOR.setName("testAuthorName");
        TEST_POST_AUTHOR.setUri("testAuthorUri");
        TEST_POST_AUTHOR.setEmail("testAuthorEmail");
    }

    protected static final PostEnclosure TEST_POST_ENCLOSURE = new PostEnclosure();
    static {
        TEST_POST_ENCLOSURE.setType("testEnclosureType");
        TEST_POST_ENCLOSURE.setUrl("testEnclosureUrl");
        TEST_POST_ENCLOSURE.setLength(4821L);
    }

    protected static final StagingPost TEST_STAGING_POST = StagingPost.from(
            "testImporterId",
            666L,
            "testImporterDesc",
            667L,
            "{}",
            "testSourceName",
            "testSourceUrl",
            TEST_POST_TITLE,
            TEST_POST_DESCRIPTION,
            List.of(TEST_POST_CONTENT),
            TEST_POST_MEDIA,
            TEST_POST_ITUNES,
            "testPostUrl",
            List.of(TEST_POST_URL),
            "testPostImgUrl",
            TEST_IMPORT_TIMESTAMP, // import timestamp
            "testPostHash",
            "me",
            "testPostComment",
            "testPostRights",
            List.of(TEST_POST_CONTRIBUTOR),
            List.of(TEST_POST_AUTHOR),
            List.of("testPostCategory"),
            TEST_PUBLISH_TIMESTAMP, // publish timestamp
            TEST_EXPIRATION_TIMESTAMP, // expiration timestamp
            List.of(TEST_POST_ENCLOSURE), // enclosures
            TEST_LAST_UPDATED_TIMESTAMP // last updated timestamp
    );
}
