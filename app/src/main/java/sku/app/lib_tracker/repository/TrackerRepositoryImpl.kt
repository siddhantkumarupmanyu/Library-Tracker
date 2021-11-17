package sku.app.lib_tracker.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sku.app.lib_tracker.api.ApiService
import sku.app.lib_tracker.db.TrackerDao
import sku.app.lib_tracker.vo.Artifact
import sku.app.lib_tracker.vo.Library
import javax.inject.Inject

// lack of better name
class TrackerRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val trackerDao: TrackerDao
) : TrackerRepository {

    override val libraries: Flow<List<Library>>
        get() = trackerDao.loadArtifacts().map { artifacts ->
            fromArtifactsToLibrary(artifacts)
        }


    // TODO: launch in parallel and cancel everyone if any exception occurs in anyone
    // throw that same exception
    override suspend fun fetchAndSave() {
        val packages = apiService.getPackages()
        val libraries = mutableListOf<Library>()
        for (p in packages) {
            val library = apiService.getLibrary(p.urlString)
            libraries.add(library)
        }
        val artifacts = libraries.flatMap {
            it.artifacts
        }
        trackerDao.insertArtifacts(artifacts)
    }

    fun fromArtifactsToLibrary(list: List<Artifact>): List<Library> {
        val libraries = mutableListOf<Library>()
        val artifactsMap = getDifferentArtifacts(list)
        for ((key, value) in artifactsMap) {
            libraries.add(Library(key, value))
        }
        return libraries
    }

    private fun getDifferentArtifacts(list: List<Artifact>): Map<String, List<Artifact>> {
        val map = mutableMapOf<String, MutableList<Artifact>>()
        for (item in list) {
            // val mutableList = map.putIfAbsent(item.packageName, mutableListOf())!! // not working
            val mutableList = map.getOrPut(item.packageName, { mutableListOf() })
            mutableList.add(item)
        }
        return map
    }


}