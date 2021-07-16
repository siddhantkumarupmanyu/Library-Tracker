package sku.app.lib_tracker

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.work.Configuration
import androidx.work.testing.WorkManagerTestInitHelper
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.fail
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import sku.app.lib_tracker.datastore.DATA_STORE_FILE_NAME
import sku.app.lib_tracker.test_utils.DisableAnimationRule
import sku.app.lib_tracker.test_utils.OkHttp3IdlingResource
import sku.app.lib_tracker.test_utils.RecyclerViewMatcher
import sku.app.lib_tracker.test_utils.enqueueResponse
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@LargeTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class EndToEndTest {

    @Rule
    @JvmField
    val disableAnimation = DisableAnimationRule()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private val mockWebServer = MockWebServer()

    @BindValue
    val okHttpClient = OkHttpClient()

    @Inject
    lateinit var workConfiguration: Configuration

    @Before
    fun startServer() {
        hiltRule.inject()

        setupRetrofitClient()

        val context = ApplicationProvider.getApplicationContext<Application>()
        WorkManagerTestInitHelper.initializeTestWorkManager(context, workConfiguration)

        mockWebServer.start(8080)
    }

    private fun setupRetrofitClient() {
        val resource = OkHttp3IdlingResource.create("okHttp", okHttpClient)
        IdlingRegistry.getInstance().register(resource)
    }

    @After
    fun stopServer() {
        mockWebServer.shutdown()

        deleteDataStoreFiles()
    }

    @Test
    fun fetchOnOpeningForFirstTimeInADay() {
        enqueueResponse()

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        assertRequestsAreMade()

        activityScenario.close()

        val activityScenario2 = ActivityScenario.launch(MainActivity::class.java)

        assertRequestsAreNotMade()

        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("activity"))))
        onView(listMatcher().atPosition(3)).check(matches(hasDescendant(withText("work-testing"))))

        activityScenario2.close()
    }

    @Test
    fun refresh() {
        enqueueResponse()

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.refresh)).perform(click())

        assertRequestsAreMade()

        // test is flaky without this
        Thread.sleep(100)

        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("activity"))))
        onView(listMatcher().atPosition(3)).check(matches(hasDescendant(withText("work-testing"))))

        activityScenario.close()
    }

    private fun deleteDataStoreFiles() {
        val appContext = ApplicationProvider.getApplicationContext<Application>()

        appContext.tempDataStoreFile(DATA_STORE_FILE_NAME).delete()
    }

    private fun assertRequestsAreMade() {
        assertRequest("/master-index.xml")

        val libraryRequest = "/androidx/%s/group-index.xml"
        assertRequest(libraryRequest.format("activity"))
        assertRequest(libraryRequest.format("fragment"))
        assertRequest(libraryRequest.format("viewpager2"))
        assertRequest(libraryRequest.format("work"))
    }

    private fun assertRequestsAreNotMade() {
        try {
            assertRequest("/master-index.xml")
            fail("Should have throw exception")
        } catch (e: Exception) {
            // no op
        }
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