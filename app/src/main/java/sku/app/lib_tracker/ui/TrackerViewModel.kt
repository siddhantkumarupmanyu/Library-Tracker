package sku.app.lib_tracker.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import sku.app.lib_tracker.datastore.DataStoreHelper
import sku.app.lib_tracker.repository.TrackerRepository
import sku.app.lib_tracker.vo.Library
import sku.app.lib_tracker.work.TrackerWorkManager
import javax.inject.Inject

@HiltViewModel
class TrackerViewModel @Inject constructor(
    private val trackerWorkManager: TrackerWorkManager,
    private val repository: TrackerRepository,
    private val dataStoreHelper: DataStoreHelper
) : ViewModel() {

    val libraries: LiveData<List<Library>> = liveData {
        repository.loadLibraries().collect {
            emit(it)
        }
    }

    init {
        viewModelScope.launch {
            if (dataStoreHelper.shouldFetch()) {
                trackerWorkManager.runFetchWorker()
                // TODO: getFetchWorkInfo
                // instead of giving away WorkerState to view
                // we gonna give a snackbar livedata
            }
        }
    }

    // (for now) fetchWorker runs only when forced to

    fun refresh(): LiveData<WorkerState> {
        trackerWorkManager.runFetchWorker()
        return trackerWorkManager.getFetchWorkInfo()
    }
}
