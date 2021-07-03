package sku.app.lib_tracker.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import sku.app.lib_tracker.repository.TrackerRepository
import sku.app.lib_tracker.vo.Library
import sku.app.lib_tracker.workers.FetchWorker
import sku.app.lib_tracker.workers.SpikeWorker
import javax.inject.Inject

@HiltViewModel
class TrackerViewModel @Inject constructor(
    private val application: Application,
    private val repository: TrackerRepository
) : ViewModel() {

    // private val _libraries = MutableLiveData<List<Library>>()
    // val libraries: LiveData<List<Library>> = _libraries

    val libraries: LiveData<List<Library>> = liveData {
        repository.loadLibraries().collect {
            // println(it)
            emit(it)
        }
    }

    private val workManager = WorkManager.getInstance(application)

    val fetchWorkInfo = workManager.getWorkInfosByTagLiveData(FETCH_WORK_TAG)

    init {
        loadLibraries()
    }

    fun loadLibraries() {
        workManager
            .beginUniqueWork(
                UNIQUE_FETCH_WORK,
                ExistingWorkPolicy.KEEP,
                OneTimeWorkRequestBuilder<FetchWorker>()
                    .addTag(FETCH_WORK_TAG)
                    .build()
            ).enqueue()
    }

    companion object {
        private const val UNIQUE_FETCH_WORK = "Unique-Fetch-Work"
        private const val FETCH_WORK_TAG = "fetch-work-tag"
    }

    fun runSpikeWorker() {
        WorkManager.getInstance(application)
            .beginUniqueWork(
                "SpikeWork",
                ExistingWorkPolicy.KEEP,
                OneTimeWorkRequestBuilder<SpikeWorker>().build()
            ).enqueue()
    }

    // private fun loadLibraries() {
    //     viewModelScope.launch {
    //         repository.fetchAndSave()
    //     }
    //     // viewModelScope.launch {
    //     //     repository.loadLibraries().collect {
    //     //         _libraries.value = it
    //     //     }
    //     // }
    // }

}