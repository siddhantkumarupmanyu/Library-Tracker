package sku.app.lib_tracker.ui

import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import sku.app.lib_tracker.R
import sku.app.lib_tracker.TestDataStoreModule
import sku.app.lib_tracker.datastore.DataStoreHelper
import sku.app.lib_tracker.di.AppModule
import sku.app.lib_tracker.di.DataStoreModule
import sku.app.lib_tracker.repository.TrackerRepository
import sku.app.lib_tracker.test_utils.*
import sku.app.lib_tracker.work.TrackerWorkManager

@RunWith(AndroidJUnit4::class)
@UninstallModules(
    AppModule::class,
    DataStoreModule::class,
    TestDataStoreModule::class
)
@HiltAndroidTest
class TrackerFragmentTest {

    @Rule
    @JvmField
    val executorRule = TaskExecutorWithIdlingResourceRule()

    @Rule
    @JvmField
    val disableAnimation = DisableAnimationRule()

    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    val repository: TrackerRepository = mock()

    @BindValue
    val trackerWorkManager: TrackerWorkManager = mock()

    @BindValue
    val helper: DataStoreHelper = mock()

    private val recyclerViewMatcher = RecyclerViewMatcher(R.id.list_view)

    private val activityLibrary = TestUtils.createLibrary("androidx.activity")
    private val roomLibrary = TestUtils.createLibrary("androidx.room")

    // DataStoreHelper values
    // since functionality to fetch on init is already tested in ViewModel's test,
    // we will keep this as false throughout these tests
    private val shouldFetch = false

    @Before
    fun setUp(): Unit = runBlocking {
        // Populate @Inject fields in test class
        hiltRule.inject()

        `when`(repository.loadLibraries()).thenReturn(flowOf(listOf(activityLibrary, roomLibrary)))

        `when`(helper.shouldFetch()).thenReturn(shouldFetch)

        launchFragmentInHiltContainer<TrackerFragment> {
            dataBindingIdlingResourceRule.monitorFragment(this)
        }
    }


    @Test
    fun librariesAreLoaded(): Unit = runBlocking {
        onView(recyclerViewMatcher.atPosition(0))
            .check(matches(hasDescendant(withText("artifact0"))))

        onView(recyclerViewMatcher.atPosition(0))
            .check(matches(hasDescendant(withText("1.3.0-beta01"))))

        onView(recyclerViewMatcher.atPosition(1))
            .check(matches(hasDescendant(withText("artifact1"))))

        onView(recyclerViewMatcher.atPosition(1))
            .check(matches(hasDescendant(withText("1.2.3"))))

        verify(repository).loadLibraries()
    }

    @Test
    fun refreshLibrariesSuccess() = runBlocking {
        val workerState = MutableLiveData(WorkerState.ENQUEUED)
        `when`(trackerWorkManager.getFetchWorkerState()).thenReturn(workerState)

        onView(withId(R.id.refresh)).perform(click())

        workerState.postValue(WorkerState.SUCCEEDED)

        onView(withText(R.string.updated_libs)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        verify(trackerWorkManager).runFetchWorker()
    }

    @Test
    fun refreshLibrariesFail_OnRetrySuccess() {
        val workerState = MutableLiveData(WorkerState.ENQUEUED)
        `when`(trackerWorkManager.getFetchWorkerState()).thenReturn(workerState)

        onView(withId(R.id.refresh)).perform(click())

        workerState.postValue(WorkerState.FAILED)

        onView(withText(R.string.update_failed)).check(matches(isDisplayed()))

        workerState.postValue(WorkerState.SUCCEEDED)

        Thread.sleep(100)

        onView(withText(R.string.retry)).perform(click())

        onView(withText(R.string.updated_libs)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        verify(trackerWorkManager, times(2)).runFetchWorker()
    }

}