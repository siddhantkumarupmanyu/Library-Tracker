package sku.app.lib_tracker.work

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.work.*
import androidx.work.testing.WorkManagerTestInitHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import sku.app.lib_tracker.repository.TrackerRepository
import sku.app.lib_tracker.ui.WorkerState
import sku.app.lib_tracker.test_utils.getOrAwaitValue
import sku.app.lib_tracker.test_utils.mock
import sku.app.lib_tracker.work.TrackerWorkManagerImpl.Companion.UNIQUE_FETCH_WORK
import sku.app.lib_tracker.work.fetch_worker.FetchWorker
import java.util.concurrent.TimeUnit

class TrackerWorkManagerImplTest {

    @Rule
    @JvmField
    val instantExecutor = InstantTaskExecutorRule()

    private val context: Application = ApplicationProvider.getApplicationContext()

    private val repository = mock<TrackerRepository>()

    private val factory = object : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker? {
            return if (workerClassName == FetchWorker::class.java.name) {
                FetchWorker(repository, appContext, workerParameters)
            } else {
                null
            }
        }

    }

    private lateinit var trackerWorkManager: TrackerWorkManagerImpl

    @Before
    fun setup() {
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            // .setExecutor(SynchronousExecutor())
            .setWorkerFactory(factory)
            .build()

        // Initialize WorkManager for instrumentation tests
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)

        trackerWorkManager = TrackerWorkManagerImpl(context)
    }

    @Test
    fun fetchWorkIsUnique() = runBlocking {
        val workManager = WorkManager.getInstance(context)

        trackerWorkManager.runFetchWorker()
        trackerWorkManager.runFetchWorker()
        trackerWorkManager.runFetchWorker()

        // testing if we always have only one work (ExistingWorkPolicy.KEEP)
        val worksInfo = workManager.getWorkInfosForUniqueWork(UNIQUE_FETCH_WORK).get()
        assertThat(worksInfo.size, `is`(1))

        // making sure work is in SUCCEEDED state when we assert it
        delay(50)

        val info = trackerWorkManager.getFetchWorkInfo().getOrAwaitValue()
        assertThat(info, `is`(WorkerState.SUCCEEDED))
    }

    @Test
    fun workIsNotRanAtLeastOnce() {
        val info = trackerWorkManager.getFetchWorkInfo().getOrAwaitValue()

        assertThat(info, `is`(WorkerState.NOT_RAN))
    }


    // SPIKES

    @Test
    fun spikeTest() {
        val request = OneTimeWorkRequestBuilder<FakeWorker>()
            .build()

        val workManager = WorkManager.getInstance(context)

        // Enqueue and wait for result. This also runs the Worker synchronously
        // because we are using a SynchronousExecutor.
        workManager.enqueue(request).result.get()

        // Get WorkInfo and outputData
        val workInfo = workManager.getWorkInfoById(request.id).get()

        assertThat(workInfo.state, `is`(equalTo(WorkInfo.State.RUNNING)))
    }

    @Test
    fun spikeTestWithDelay() {
        val request = OneTimeWorkRequestBuilder<FakeWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS)
            .addTag("fake-tag")
            .build()

        val workManager = WorkManager.getInstance(context)

        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)!!

        workManager
            .beginUniqueWork("unique-fake-work", ExistingWorkPolicy.KEEP, request)
            .enqueue().result.get()

        var workInfo = workManager.getWorkInfoById(request.id).get()
        assertThat(workInfo.state, `is`(equalTo(WorkInfo.State.ENQUEUED)))

        testDriver.setInitialDelayMet(request.id)

        workInfo = workManager.getWorkInfoById(request.id).get()
        assertThat(workInfo.state, `is`(equalTo(WorkInfo.State.RUNNING)))

        val worksInfo = workManager.getWorkInfosForUniqueWork("unique-fake-work").get()
        assertThat(worksInfo.size, `is`(1))
    }


    class FakeWorker(
        appContext: Context,
        workerParams: WorkerParameters
    ) : CoroutineWorker(appContext, workerParams) {
        override suspend fun doWork(): Result {
            delay(100)
            return Result.success(workDataOf("data" to "done"))
        }

    }

}