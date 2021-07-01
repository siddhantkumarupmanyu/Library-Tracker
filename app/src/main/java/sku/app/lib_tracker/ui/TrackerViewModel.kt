package sku.app.lib_tracker.ui

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import sku.app.lib_tracker.repository.TrackerRepository
import sku.app.lib_tracker.vo.Library
import javax.inject.Inject

@HiltViewModel
class TrackerViewModel @Inject constructor(
    private val repository: TrackerRepository
) : ViewModel() {

    // private val _libraries = MutableLiveData<List<Library>>()
    // val libraries: LiveData<List<Library>> = _libraries

    val libraries: LiveData<List<Library>> = liveData {
        repository.loadLibraries().collect {
            println(it)
            emit(it)
        }
    }

    fun loadLibraries() {
        viewModelScope.launch {
            repository.fetchAndSave()
        }
        // viewModelScope.launch {
        //     repository.loadLibraries().collect {
        //         _libraries.value = it
        //     }
        // }
    }

}