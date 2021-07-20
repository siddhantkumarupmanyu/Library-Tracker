package sku.app.lib_tracker.work.fetch_worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import sku.app.lib_tracker.datastore.DataStoreHelper
import sku.app.lib_tracker.repository.TrackerRepository

class FetchWorkerFactory(
    private val repository: TrackerRepository,
    private val dataStoreHelper: DataStoreHelper
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return if (workerClassName == FetchWorker::class.java.name) {
            FetchWorker(repository, dataStoreHelper, appContext, workerParameters)
        } else {
            null
        }
    }

}
