package sku.app.lib_tracker.work

import androidx.lifecycle.LiveData
import androidx.work.WorkInfo
import javax.inject.Inject


// lack of better name
class TrackerWorkManagerImpl @Inject constructor() : TrackerWorkManager {

    // private val workManager = WorkManager.getInstance(application)



    // i could test these functions just have to see work docs once (again)
    override fun runFetchWorker() {
        TODO("Not yet implemented")
        // workManager
        //     .beginUniqueWork(
        //         UNIQUE_FETCH_WORK,
        //         ExistingWorkPolicy.KEEP,
        //         OneTimeWorkRequestBuilder<FetchWorker>()
        //             .addTag(FETCH_WORK_TAG)
        //             .build()
        //     ).enqueue()
    }

    override fun getFetchWorkInfo(): LiveData<WorkInfo.State> {
        TODO("Not yet implemented")
        // val fetchWorkerState = workManager.getWorkInfosByTagLiveData(FETCH_WORK_TAG).map { infos ->
        //     if (workerRan) {
        //         infos[0].state.workerState()
        //     } else {
        //         WorkerState.NOT_RAN
        //     }
        // }
    }
    // fun runSpikeWorker() {
    //     WorkManager.getInstance(application)
    //         .beginUniqueWork(
    //             "SpikeWork",
    //             ExistingWorkPolicy.KEEP,
    //             OneTimeWorkRequestBuilder<SpikeWorker>().build()
    //         ).enqueue()
    // }


    companion object {
        private const val UNIQUE_FETCH_WORK = "Unique-Fetch-Work"
        private const val FETCH_WORK_TAG = "fetch-work-tag"
    }

}