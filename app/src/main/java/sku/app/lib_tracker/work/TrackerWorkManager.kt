package sku.app.lib_tracker.work

import androidx.lifecycle.LiveData
import androidx.work.WorkInfo
import sku.app.lib_tracker.ui.WorkerState

interface TrackerWorkManager {

    fun runFetchWorker()

    fun getFetchWorkInfo(): LiveData<WorkerState>

}