package sku.app.lib_tracker.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import sku.app.lib_tracker.repository.TrackerRepository
import sku.app.lib_tracker.utils.MainCoroutineRule
import sku.app.lib_tracker.utils.TestUtils
import sku.app.lib_tracker.utils.getOrAwaitValue
import sku.app.lib_tracker.utils.mock
import sku.app.lib_tracker.vo.Library
import sku.app.lib_tracker.work.TrackerWorkManager

class TrackerViewModelTest {

    @Rule
    @JvmField
    val instantExecutor = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var repository: TrackerRepository

    private lateinit var manager: TrackerWorkManager

    private lateinit var viewModel: TrackerViewModel

    private val activityLibrary = TestUtils.createLibrary("androidx.activity")
    private val roomLibrary = TestUtils.createLibrary("androidx.room")

    private val workerInfoState = MutableLiveData(WorkerState.NOT_RAN)

    @Before
    fun setup() {
        repository = mock()
        manager = mock()

        `when`(manager.getFetchWorkInfo()).thenReturn(workerInfoState)

        `when`(manager.runFetchWorker()).then {
            workerInfoState.postValue(WorkerState.ENQUEUED)
        }

        viewModel = TrackerViewModel(manager, repository)
    }

    @Test
    fun loadLibraries(): Unit = runBlocking {
        val flow = flowOf(listOf(activityLibrary, roomLibrary))
        `when`(repository.loadLibraries()).thenReturn(flow)

        assertThat(
            viewModel.libraries.getOrAwaitValue(),
            `is`(equalTo(listOf(activityLibrary, roomLibrary)))
        )

        verify(repository).loadLibraries()
    }

    @Test
    fun runFetchWorker() {
        val observer = mock<Observer<WorkerState>>()
        viewModel.refresh().observeForever(observer)

        verify(observer).onChanged(WorkerState.ENQUEUED)

        workerInfoState.postValue(WorkerState.RUNNING)
        verify(observer).onChanged(WorkerState.RUNNING)

        workerInfoState.postValue(WorkerState.SUCCEEDED)
        verify(observer).onChanged(WorkerState.SUCCEEDED)

        verify(manager).getFetchWorkInfo()
        verify(manager).runFetchWorker()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun reflectDataUpdate(): Unit = mainCoroutineRule.runBlockingTest {
        val updateActivityLib = Library(
            "androidx.activity",
            listOf(TestUtils.createArtifact("activity", "androidx.activity"))
        )

        val updateRoomLib = Library(
            "androidx.room",
            listOf(TestUtils.createArtifact("room", "androidx.room"))
        )

        val libs = flow {
            emit(listOf(activityLibrary, roomLibrary))
            delay(100)
            emit(listOf(updateActivityLib, updateRoomLib))
        }

        `when`(repository.loadLibraries()).thenReturn(libs)

        val observer = mock<Observer<List<Library>>>()

        viewModel.libraries.observeForever(observer)

        verify(observer).onChanged(listOf(activityLibrary, roomLibrary))

        advanceUntilIdle()

        verify(observer).onChanged(listOf(updateActivityLib, updateRoomLib))
        verify(repository).loadLibraries()
    }

}