package sku.app.lib_tracker.work

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import sku.app.lib_tracker.repository.TrackerRepository
import sku.app.lib_tracker.utils.mock
import sku.app.lib_tracker.work.fetch_worker.FetchWorker


@RunWith(AndroidJUnit4::class)
class FetchWorkerTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    private lateinit var worker: FetchWorker

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

    @Before
    fun setUp() {
        worker = TestListenableWorkerBuilder<FetchWorker>(context)
            .setWorkerFactory(factory)
            .build()
    }

    @Test
    fun fetchAndSave() = runBlocking {
        worker.doWork()

        verify(repository).fetchAndSave()
    }

    // TODO: add test for notification


}