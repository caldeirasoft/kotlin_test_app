package com.caldeirasoft.basicapp;

import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.caldeirasoft.basicapp.api.itunes.data.Search

import junit.framework.Assert

import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ItunesApiTest {
    private val TAG = "API_TEST_TOP"
    private val MEDIA_COUNTRY = "fr"
    private val SEARCH_LIMIT = 15
    private val SEARCH_TERM = "RTL"

    @Test
    @Throws(Exception::class)
    fun testTopApiCall() {
        val service = ITunesSearchService()
        val entries = service.topPodcasts(MEDIA_COUNTRY, SEARCH_LIMIT);
        Assert.assertNotNull(entries)
        Log.d(TAG, "testApiCall: " + entries?.size)
        entries?.forEach { entry ->
            Assert.assertNotNull(entry)
            Log.d(TAG, entry.imName.label + " - " + entry.imArtist.label)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testSearchApiCall() {

        val service = ITunesSearchService()
        val searchResult: Search? = service.searchPodcasts(SEARCH_TERM);
        Assert.assertNotNull(searchResult)
        var podcasts = searchResult?.results
        Assert.assertNotNull(podcasts)
        Log.d(TAG, "testApiCall: " + podcasts?.size)
        podcasts?.forEach { podcast ->
            Assert.assertNotNull(podcast)
            Log.d(TAG, podcast.artistName + " " + podcast.trackName)
        }
    }

}
