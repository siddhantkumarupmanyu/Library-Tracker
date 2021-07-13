package sku.app.lib_tracker.work

import androidx.lifecycle.LiveData
import androidx.work.WorkInfo

interface TrackerWorkManager {

    fun runFetchWorker()

    fun getFetchWorkInfo(): LiveData<WorkInfo.State>

}