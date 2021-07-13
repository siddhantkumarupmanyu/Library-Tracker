package sku.app.lib_tracker

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import sku.app.lib_tracker.utils.RecyclerViewMatcher
import sku.app.lib_tracker.utils.enqueueResponse
import java.util.concurrent.TimeUnit

@LargeTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class EndToEndTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private val mockWebServer = MockWebServer()

    @Before
    fun startServer() {
        mockWebServer.start(8080)
    }

    @After
    fun stopServer() {
        mockWebServer.shutdown()
    }

    // TODO: shouldn't I use in memory database here

    @Test
    fun simpleFetch() {
        enqueueResponse()

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        assertRequestsAreMade()

        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("activity"))))
        onView(listMatcher().atPosition(3)).check(matches(hasDescendant(withText("work-testing"))))

        activityScenario.close()
    }

    private fun assertRequestsAreMade() {
        assertRequest("/master-index.xml")

        val libraryRequest = "/androidx/%s/group-index.xml"
        assertRequest(libraryRequest.format("activity"))
        assertRequest(libraryRequest.format("fragment"))
        assertRequest(libraryRequest.format("viewpager2"))
        assertRequest(libraryRequest.format("work"))
    }

    private fun assertRequest(path: String) {
        val request = mockWebServer.takeRequest(2, TimeUnit.SECONDS)
        assertThat(request.path, `is`(path))
    }

    private fun enqueueResponse() {
        mockWebServer.enqueueResponse("master-index-small.xml")
        mockWebServer.enqueueResponse("activity-group-index.xml")
        mockWebServer.enqueueResponse("fragment-group-index.xml")
        mockWebServer.enqueueResponse("viewpager2-group-index.xml")
        mockWebServer.enqueueResponse("work-group-index.xml")
    }

    private fun listMatcher(): RecyclerViewMatcher {
        return RecyclerViewMatcher(R.id.list_view)
    }

}