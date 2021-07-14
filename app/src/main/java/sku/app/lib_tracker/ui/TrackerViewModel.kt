package sku.app.lib_tracker.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
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

    val fetchWorkerState = trackerWorkManager.getFetchWorkInfo()

    // (for now) fetchWorker runs only when forced to
    // TODO: menu item for force fetch

    fun runFetch() {
        trackerWorkManager.runFetchWorker()
    }
}
