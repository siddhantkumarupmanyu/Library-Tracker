package sku.app.lib_tracker.ui

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import sku.app.lib_tracker.datastore.DataStoreHelper
import sku.app.lib_tracker.repository.TrackerRepository
import sku.app.lib_tracker.vo.Event
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

    val events = MutableLiveData<Event<Any>>()

    init {
        viewModelScope.launch {
            if (dataStoreHelper.shouldFetch()) {
                trackerWorkManager.runFetchWorker()
                val stateLiveData = trackerWorkManager.getFetchWorkInfo()
                WorkStateObserver(stateLiveData)
            }
        }
    }

    fun refresh() {
        trackerWorkManager.runFetchWorker()
        val stateLiveData = trackerWorkManager.getFetchWorkInfo()
        WorkStateObserver(stateLiveData)
    }

    // worker state observer
    // removes itself after finishing
    private inner class WorkStateObserver(
        private val workerStateLiveData: LiveData<WorkerState>
    ) : Observer<WorkerState> {

        init {
            workerStateLiveData.observeForever(this)
        }

        override fun onChanged(state: WorkerState) {
            events.postValue(Event(state))
            if (state.isFinished()) {
                workerStateLiveData.removeObserver(this)
            }
        }
    }
}
