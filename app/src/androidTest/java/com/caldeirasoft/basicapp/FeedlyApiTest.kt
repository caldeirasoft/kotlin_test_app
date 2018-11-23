package com.caldeirasoft.basicapp;

import android.support.test.runner.AndroidJUnit4
import android.util.Log

import junit.framework.Assert
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class FeedlyApiTest {
    private val TAG = "API_TEST_GET_FEED"
    private val FEEDID = "feed/http://radiofrance-podcast.net/podcast09/rss_16723.xml"

    private val FEEDID_1 = "feed/http://radiofrance-podcast.net/podcast09/rss_16723.xml"
    private val FEEDID_2 = "feed/http://canal42.tv/podcast/feed.xml"
    private val FEEDID_3 = "feed/http://feeds.feedburner.com/podcastmrg"

    private lateinit var service:FeedlyService

    @Before
    fun setup()
    {
        service = FeedlyService()
    }

    @Test
    @Throws(Exception::class)
    fun testFeedlyGetFeed()
    {
        val service = FeedlyService()
        val feed = service.getFeed(FEEDID);
        Assert.assertNotNull(feed)
        Log.d(TAG, "testFeedlyGetFeed: ")

        Log.d(TAG, "feedId: " + feed?.feedId)
        Log.d(TAG, "name: " + feed?.name)
        Log.d(TAG, "coverUrl: " + feed?.coverUrl)
        Log.d(TAG, "description: " + feed?.description)
        Log.d(TAG, "image: " + feed?.image)
        Log.d(TAG, "language: " + feed?.language)
        Log.d(TAG, "state: " + feed?.state)
        Log.d(TAG, "updated: " + feed?.updated)
        Log.d(TAG, "velocity: " + feed?.velocity)

        Log.d(TAG, "========================\n");
        Assert.assertEquals(feed?.feedId, FEEDID)
    }

    @Test
    @Throws(Exception::class)
    fun testFeedlyGetFeeds()
    {
        val service = FeedlyService()
        var feedIds:List<String> = mutableListOf(FEEDID_1, FEEDID_2, FEEDID_3)
        val feeds = service.getFeeds(feedIds);
        Assert.assertNotNull(feeds)
        Log.d(TAG, "testFeedlyGetFeeds: " + feeds?.size)
        Assert.assertEquals(feeds?.size, 3)
        feeds?.forEach { feed ->
            Assert.assertNotNull(feed)
            Log.d(TAG, "feedId: " + feed?.feedId)
            Log.d(TAG, "name: " + feed?.name)
            Log.d(TAG, "coverUrl: " + feed?.coverUrl)
            Log.d(TAG, "description: " + feed?.description)
            Log.d(TAG, "image: " + feed?.image)
            Log.d(TAG, "language: " + feed?.language)
            Log.d(TAG, "state: " + feed?.state)
            Log.d(TAG, "updated: " + feed?.updated)
            Log.d(TAG, "velocity: " + feed?.velocity)
            Log.d(TAG, "---------------------\n")
        }

        Log.d(TAG, "========================\n")
    }

    @Test
    @Throws(Exception::class)
    fun testFeedlyGetFeedEntries()
    {
        val service = FeedlyService()
        val feedResponse = service.getStreamEntries(FEEDID, 20);
        Assert.assertNotNull(feedResponse)
        val feedId = feedResponse?.id
        Log.d(TAG, "feedId: " + feedResponse?.id)
        Log.d(TAG, "title: " + feedResponse?.title)
        Log.d(TAG, "continuation: " + feedResponse?.continuation)
        Log.d(TAG, "updated: " + feedResponse?.updated)
        var entries = feedResponse?.items
        Assert.assertNotNull(entries)
        Log.d(TAG, "testFeedlyGetFeedEntries: " + entries?.size)
        Assert.assertEquals(entries?.size, 20)
        entries?.forEach { entry ->
            Assert.assertNotNull(entry)
            Log.d(TAG, "id: " + entry?.id)
            Log.d(TAG, "title: " + entry?.title)
            Log.d(TAG, "author: " + entry?.author)
            Log.d(TAG, "content: " + entry?.content?.content)
            Log.d(TAG, "summary: " + entry?.summary?.content)
            Log.d(TAG, "crawled: " + entry?.crawled)
            Log.d(TAG, "updated: " + entry?.updated)
            Log.d(TAG, "published: " + entry?.published)

            Assert.assertNotNull(entry.origin)
            Log.d(TAG, "origin.streamId: " + entry.origin?.streamId)
            Log.d(TAG, "origin.title: " + entry.origin?.title)
            Log.d(TAG, "origin.htmlurl: " + entry.origin?.htmlUrl)

            Assert.assertNotNull(entry.enclosure)
            var i:Int = 0
            entry.enclosure.forEach{ link ->
                i++
                Log.d(TAG, "origin.enclosure.href " + i + " : " + link.href)
                Log.d(TAG, "origin.enclosure.type " + i + " : " + link.type)
                Log.d(TAG, "origin.enclosure.length " + i + " : " + link.length)
            }

            Log.d(TAG, "---------------------\n")
        }

        Log.d(TAG, "========================\n")
    }
}
