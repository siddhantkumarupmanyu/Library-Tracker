package sku.app.lib_tracker.repository

import kotlinx.coroutines.flow.Flow
import sku.app.lib_tracker.vo.Library

interface TrackerRepository {

    val libraries: Flow<List<Library>>

    suspend fun fetchAndSave()
}