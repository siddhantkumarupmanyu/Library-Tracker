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
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import sku.app.lib_tracker.datastore.DataStoreHelper
import sku.app.lib_tracker.repository.TrackerRepository
import sku.app.lib_tracker.test_utils.MainCoroutineRule
import sku.app.lib_tracker.test_utils.TestUtils
import sku.app.lib_tracker.test_utils.getOrAwaitValue
import sku.app.lib_tracker.test_utils.mock
import sku.app.lib_tracker.vo.Event
import sku.app.lib_tracker.vo.Library
import sku.app.lib_tracker.vo.getEventValue
import sku.app.lib_tracker.work.TrackerWorkManager
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class TrackerViewModelTest {

    @Rule
    @JvmField
    val instantExecutor = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var repository: TrackerRepository
    private lateinit var manager: TrackerWorkManager
    private lateinit var helper: DataStoreHelper

    private lateinit var viewModel: TrackerViewModel

    private val activityLibrary = TestUtils.createLibrary("androidx.activity")
    private val roomLibrary = TestUtils.createLibrary("androidx.room")

    private val workerInfoState = MutableLiveData(WorkerState.NOT_RAN)

    @Before
    fun setup(): Unit = runBlocking {
        repository = mock()
        manager = mock()
        helper = mock()

        `when`(manager.getFetchWorkerState()).thenReturn(workerInfoState)

        `when`(manager.runFetchWorker()).then {
            workerInfoState.postValue(WorkerState.ENQUEUED)
        }

        `when`(helper.shouldFetch()).thenReturn(false)

        viewModel = TrackerViewModel(manager, repository, helper)
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

    @Test
    fun runFetchWorkerOnRefresh() {
        val observer = mock<Observer<Event<Any>>>()
        viewModel.events.observeForever(observer)

        viewModel.refresh()

        assertThat(viewModel.events.getEventValue(), `is`(WorkerState.ENQUEUED))

        // should I use captor method
//        val captor = argumentCaptor<Event<WorkerState>>()

//        verify(observer, times(1)).onChanged(captor.capture())
//        assertThat(captor.value.peekContent(), `is`(WorkerState.ENQUEUED))

        workerInfoState.postValue(WorkerState.RUNNING)
//        verify(observer, times(2)).onChanged(captor.capture())
//        assertThat(captor.value.peekContent(), `is`(WorkerState.RUNNING))
        assertThat(viewModel.events.getEventValue(), `is`(WorkerState.RUNNING))

        workerInfoState.postValue(WorkerState.SUCCEEDED)
//        verify(observer, times(3)).onChanged(captor.capture())
//        assertThat(captor.value.peekContent(), `is`(WorkerState.SUCCEEDED))
        assertThat(viewModel.events.getEventValue(), `is`(WorkerState.SUCCEEDED))

        verify(manager).getFetchWorkerState()
        verify(manager).runFetchWorker()
    }

    // shouldFetch = false already being tested in refresh
    @Test
    fun runFetchWorkerOnInitialization() = mainCoroutineRule.runBlockingTest {
        reset(helper)

        `when`(helper.shouldFetch()).thenReturn(true)

        val viewModel = TrackerViewModel(manager, repository, helper)

        assertThat(viewModel.events.getEventValue(), `is`(equalTo(WorkerState.ENQUEUED)))

        verify(helper).shouldFetch()
        verify(manager).runFetchWorker()
    }

    @Test
    fun shouldNotGetAnyEventAfterWorkIsFinished() {
        val observer = mock<Observer<Event<Any>>>()
        viewModel.events.observeForever(observer)

        viewModel.refresh()

        assertThat(viewModel.events.getEventValue(), `is`(WorkerState.ENQUEUED))

        workerInfoState.postValue(WorkerState.SUCCEEDED)
        assertThat(viewModel.events.getEventValue(), `is`(WorkerState.SUCCEEDED))

        workerInfoState.postValue(WorkerState.UNKNOWN_STATE)
        assertThat(
            viewModel.events.getEventValue(timeout = 100, timeUnit = TimeUnit.MILLISECONDS),
            `is`(nullValue()) // since we have already consumed last Succeeded event, see getEventValue
        )

        verify(manager).getFetchWorkerState()
        verify(manager).runFetchWorker()
    }

}