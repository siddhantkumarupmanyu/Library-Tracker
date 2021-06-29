package sku.app.lib_tracker.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import sku.app.lib_tracker.vo.Package
import javax.inject.Inject

@HiltViewModel
class TrackerViewModel @Inject constructor() : ViewModel() {

    val testData = MutableLiveData<List<Package>>()

    init {

        val libs = getLibs()

        testData.postValue(libs)

    }

    private fun getLibs(): List<Package> {
        return MutableList(20) {
            Package("name$it")
        }
    }
}