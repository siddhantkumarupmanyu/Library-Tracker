package sku.app.lib_tracker.work

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import sku.app.lib_tracker.datastore.DataStoreHelper
import sku.app.lib_tracker.repository.TrackerRepository
import sku.app.lib_tracker.test_utils.mock
import sku.app.lib_tracker.work.fetch_worker.FetchWorker


@RunWith(AndroidJUnit4::class)
class FetchWorkerTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    private lateinit var worker: FetchWorker

    private val repository = mock<TrackerRepository>()

    private val helper = mock<DataStoreHelper>()

    private val factory = object : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker? {
            return if (workerClassName == FetchWorker::class.java.name) {
                FetchWorker(repository, helper, appContext, workerParameters)
            } else {
                null
            }
        }

    }

    @Before
    fun setUp() {

        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)

        worker = TestListenableWorkerBuilder<FetchWorker>(context)
            .setWorkerFactory(factory)
            .build()
    }

    @Test
    fun fetchAndSaveLastFetchDate() = runBlocking {
        worker.doWork()

        verify(repository).fetchAndSave()
        verify(helper).saveLastFetchDate()
    }

    @Test
    fun returnResultFailureInCaseOfAnyException(): Unit = runBlocking {
        `when`(repository.fetchAndSave()).thenThrow(RuntimeException("unable to complete the request"))

        val result = worker.doWork()

        assertThat(result, `is`(ListenableWorker.Result.failure()))

        verify(repository).fetchAndSave()
        verifyNoInteractions(helper)
    }

}