package sku.app.lib_tracker

import android.app.Application
import androidx.annotation.StringRes
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import androidx.work.Configuration
import androidx.work.testing.WorkManagerTestInitHelper
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import sku.app.lib_tracker.utils.DisableAnimationRule
import javax.inject.Inject

@LargeTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class NotificationTest {

    private val timeout = 2000L // 2 sec

    private val mockWebServer = MockWebServer()

    @Rule
    @JvmField
    val disableAnimation = DisableAnimationRule()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    val okHttpClient = OkHttpClient()

    @Inject
    lateinit var workConfiguration: Configuration

    private lateinit var context: Application

    private lateinit var scenerio: ActivityScenario<MainActivity>

    private lateinit var uiDevice: UiDevice

    @Before
    fun startServer() {
        mockWebServer.start(8080)
    }

    @Before
    fun setup() {
        hiltRule.inject()

        context = ApplicationProvider.getApplicationContext()
        WorkManagerTestInitHelper.initializeTestWorkManager(context, workConfiguration)

        scenerio = ActivityScenario.launch(MainActivity::class.java)

        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @After
    fun teardown() {
        scenerio.close()
    }

    @After
    fun stopServer() {
        mockWebServer.shutdown()
    }

    @Test
    fun notificationIsShowOnRefresh() {
        onView(withId(R.id.refresh)).perform(click())

        val expectedAppName = getString(R.string.app_name)
        val expectedTitle = getString(R.string.notification_title)

        uiDevice.openNotification()

        uiDevice.wait(Until.hasObject(By.text(expectedAppName)), timeout)

        val title = uiDevice.findObject(By.text(expectedTitle))

        assertThat(title.text, `is`(equalTo(expectedTitle)))

        closeNotification()
    }

    @Test
    fun showErrorOnCancellation() {
        val expectedAppName = getString(R.string.app_name)

        onView(withId(R.id.refresh)).perform(click())

        uiDevice.openNotification()

        uiDevice.wait(Until.hasObject(By.text(expectedAppName)), timeout)

        val cancelAction = uiDevice.findObject(By.text(getString(R.string.notification_cancel)))

        cancelAction.click()

        closeNotification()

        onView(withText(R.string.update_failed))
            .check(matches(isDisplayed()))

    }

    @Test
    fun notificationShownOnRetry() {
        val expectedAppName = getString(R.string.app_name)

        onView(withId(R.id.refresh)).perform(click())

        uiDevice.openNotification()

        uiDevice.wait(Until.hasObject(By.text(expectedAppName)), timeout)

        val cancelAction = uiDevice.findObject(By.text(getString(R.string.notification_cancel)))
        cancelAction.click()

        closeNotification()

        // onView(withText(R.string.retry))
        //     .perform(click())

        val retry = getString(R.string.retry).uppercase()

        uiDevice.wait(Until.hasObject(By.text(retry)), timeout)

        val retryAction = uiDevice.findObject(By.text(retry))
        retryAction.click()

        uiDevice.openNotification()

        uiDevice.wait(Until.hasObject(By.text(expectedAppName)), timeout)

        val title = uiDevice.findObject(By.text(getString(R.string.notification_title)))

        assertThat(title.text, `is`(equalTo(getString(R.string.notification_title))))

        closeNotification()
    }

    private fun getString(@StringRes id: Int): String {
        return context.getString(id)
    }


    private fun closeNotification() {
        uiDevice.pressBack()
    }

}


// ----------------------------------------------------------
//
// these are end to end test focused on notification
// since notification need to operate in parallel while
// fetching. It does not have idling resource for the okhttp client
//
// ----------------------------------------------------------
//
// why i am thinking that this test should be in TrackerFragment Test
// no it should not; it should be in end to end test only
// since we are testing fragment in isolation(sort of)
// we have already tested that it should display error snackbar when
// workInfo failed.
// So I think it should be here only in end to end test

