package sku.app.lib_tracker.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withText
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
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import sku.app.lib_tracker.R
import sku.app.lib_tracker.di.RepositoryModule
import sku.app.lib_tracker.repository.TrackerRepository
import sku.app.lib_tracker.utils.*

@RunWith(AndroidJUnit4::class)
@UninstallModules(RepositoryModule::class)
@HiltAndroidTest
class TrackerFragmentTest {

    @Rule
    @JvmField
    val executorRule = TaskExecutorWithIdlingResourceRule()

    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    val repository: TrackerRepository = mock()

    private val recyclerViewMatcher = RecyclerViewMatcher(R.id.list_view)

    private val activityLibrary = TestUtils.createLibrary("androidx.activity")
    private val roomLibrary = TestUtils.createLibrary("androidx.room")

    @Before
    fun setUp() {
        // Populate @Inject fields in test class
        hiltRule.inject()

        `when`(repository.loadLibraries()).thenReturn(flowOf(listOf(activityLibrary, roomLibrary)))

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

        verify(repository).fetchAndSave()
        verify(repository).loadLibraries()
    }

}