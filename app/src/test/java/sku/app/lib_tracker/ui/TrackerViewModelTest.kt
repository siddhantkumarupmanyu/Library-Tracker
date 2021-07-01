package sku.app.lib_tracker.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import sku.app.lib_tracker.repository.TrackerRepository
import sku.app.lib_tracker.utils.MainCoroutineRule
import sku.app.lib_tracker.utils.TestUtils
import sku.app.lib_tracker.utils.getOrAwaitValue
import sku.app.lib_tracker.utils.mock
import sku.app.lib_tracker.vo.Library

class TrackerViewModelTest {

    @Rule
    @JvmField
    val instantExecutor = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val repository = mock<TrackerRepository>()

    private val viewModel by lazy {
        TrackerViewModel(repository)
    }

    private val activityLibrary = TestUtils.createLibrary("androidx.activity")
    private val roomLibrary = TestUtils.createLibrary("androidx.room")


    @Test
    fun loadLibraries(): Unit = runBlocking {
        val flow = flowOf(listOf(activityLibrary, roomLibrary))
        `when`(repository.loadLibraries()).thenReturn(flow)

        viewModel.loadLibraries()

        assertThat(
            viewModel.libraries.getOrAwaitValue(),
            `is`(equalTo(listOf(activityLibrary, roomLibrary)))
        )

        verify(repository).fetchAndSave()
        verify(repository).loadLibraries()
    }

    // IDK what i am missing in this test
   /* @ExperimentalCoroutinesApi
    @Test
    fun reflectDataUpdate() = runBlockingTest {
        val updateActivityLib = Library(
            "androidx.activity",
            listOf(TestUtils.createArtifact("activity", "androidx.activity"))
        )

        val updateRoomLib = Library(
            "androidx.room",
            listOf(TestUtils.createArtifact("room", "androidx.room"))
        )

        val flow = flow {
            delay(50)
            emit(listOf(activityLibrary, roomLibrary))
            delay(2000)
            emit(listOf(updateActivityLib, updateRoomLib))
        }
        `when`(repository.loadLibraries()).thenReturn(flow)

        viewModel.loadLibraries()

        val observer = mock<Observer<List<Library>>>()

        assertThat(
            viewModel.libraries.getOrAwaitValue(5),
            `is`(equalTo(listOf(activityLibrary, roomLibrary)))
        )

        // viewModel.libraries.observeForever(observer)

        // advanceTimeBy(2000)

        // delay(2200)

        // assertThat(
        //     viewModel.libraries.getOrAwaitValue(),
        //     `is`(equalTo(listOf(updateActivityLib, updateRoomLibrary)))
        // )

        // verify(observer).onChanged(listOf(activityLibrary, roomLibrary))
        // verify(observer).onChanged(listOf(updateActivityLib, updateRoomLib))

        verify(repository, times(1)).fetchAndSave()
        verify(repository).loadLibraries()
    }*/

}