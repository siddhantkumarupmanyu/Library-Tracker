package sku.app.lib_tracker.repository

import kotlinx.coroutines.flow.Flow
import sku.app.lib_tracker.vo.Library

interface TrackerRepository {

    val libraries: Flow<List<Library>>

    // todo: Rename To fetchLibraries
    suspend fun fetchAndSave()
    // fun loadLibraries(): Flow<List<Library>>
}