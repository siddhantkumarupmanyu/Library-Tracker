package sku.app.lib_tracker.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import sku.app.lib_tracker.repository.TrackerRepository
import sku.app.lib_tracker.vo.Library
import sku.app.lib_tracker.work.TrackerWorkManager
import javax.inject.Inject

@HiltViewModel
class TrackerViewModel @Inject constructor(
    private val trackerWorkManager: TrackerWorkManager,
    private val repository: TrackerRepository
) : ViewModel() {

    val libraries: LiveData<List<Library>> = liveData {
        repository.loadLibraries().collect {
            emit(it)
        }
    }

    private var workerRan = false

    // TODO:
    // val fetchWorkerState = trackerWorkManager.getFetchWorkInfo().map { infoState ->
    //     if (workerRan) {
    //         infoState.workerState()
    //     } else {
    //         WorkerState.NOT_RAN
    //     }
    // }

    // (for now) fetchWorker runs only when forced to

    fun runFetch() {
        trackerWorkManager.runFetchWorker()
        workerRan = true
    }
}
