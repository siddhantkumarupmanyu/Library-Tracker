package sku.app.lib_tracker.work

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import sku.app.lib_tracker.ui.WorkerState
import sku.app.lib_tracker.ui.workerState
import sku.app.lib_tracker.work.fetch_worker.FetchWorker
import javax.inject.Inject


// lack of better name
class TrackerWorkManagerImpl @Inject constructor(
    private val application: Application
) : TrackerWorkManager {

    private val workManager = WorkManager.getInstance(application)

    private var enqueuedOnce: Boolean = false

    override fun runFetchWorker() {
        workManager
            .beginUniqueWork(
                UNIQUE_FETCH_WORK,
                ExistingWorkPolicy.KEEP,
                OneTimeWorkRequestBuilder<FetchWorker>()
                    .addTag(FETCH_WORK_TAG)
                    .build()
            ).enqueue()
        enqueuedOnce = true
    }

    override fun getFetchWorkInfo(): LiveData<WorkerState> {
        return workManager.getWorkInfosByTagLiveData(FETCH_WORK_TAG).map {
            val state = if (enqueuedOnce) {
                it[0].state.workerState()
            } else {
                WorkerState.NOT_RAN
            }
            state
        }
    }

    companion object {
        const val UNIQUE_FETCH_WORK = "Unique-Fetch-Work"
        const val FETCH_WORK_TAG = "fetch-work-tag"
    }

}