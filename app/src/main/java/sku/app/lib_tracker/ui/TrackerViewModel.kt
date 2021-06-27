package sku.app.lib_tracker.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import sku.app.lib_tracker.Library
import javax.inject.Inject

@HiltViewModel
class TrackerViewModel @Inject constructor() : ViewModel() {

    val testData = MutableLiveData<List<Library>>()

    init {

        val libs = getLibs()

        testData.postValue(libs)

    }

    private fun getLibs(): List<Library> {
        return MutableList(20) {
            Library("name$it", "version$it")
        }
    }
}