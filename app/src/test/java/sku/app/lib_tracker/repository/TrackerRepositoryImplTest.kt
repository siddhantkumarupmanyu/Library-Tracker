package sku.app.lib_tracker.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import sku.app.lib_tracker.api.ApiService
import sku.app.lib_tracker.db.TrackerDao
import sku.app.lib_tracker.test_utils.TestUtils
import sku.app.lib_tracker.test_utils.mock
import sku.app.lib_tracker.vo.Artifact
import sku.app.lib_tracker.vo.Library
import sku.app.lib_tracker.vo.Package

class TrackerRepositoryImplTest {

    private val apiService = mock<ApiService>()

    private val trackerDao = mock<TrackerDao>()

    private val repository = TrackerRepositoryImpl(apiService, trackerDao)


    private val activityPackageName = "androidx.activity"
    private val roomPackageName = "androidx.room"

    private val activityArtifact = TestUtils.createArtifact("activity-compose", activityPackageName)
    private val roomArtifact = TestUtils.createArtifact("room", roomPackageName)


    @Test
    fun fetchAndInsertArtifacts() = runBlocking {
        `when`(apiService.getPackages()).thenReturn(
            listOf(Package(activityPackageName), Package(roomPackageName))
        )
        `when`(apiService.getLibrary("androidx/activity")).thenReturn(
            Library(activityPackageName, listOf(activityArtifact))
        )
        `when`(apiService.getLibrary("androidx/room")).thenReturn(
            Library(roomPackageName, listOf(roomArtifact))
        )

        repository.fetchAndSave()

        verify(apiService).getPackages()
        verifyGetLibrary("activity")
        verifyGetLibrary("room")
        verify(trackerDao).insertArtifacts(listOf(activityArtifact, roomArtifact))
    }

    @Test
    fun loadAndMapArtifacts(): Unit = runBlocking {
        val flow = flowOf(listOf(activityArtifact, roomArtifact))
        `when`(trackerDao.loadArtifacts()).thenReturn(flow)

        val actual = repository.loadLibraries().first()


        assertLibraries(
            actual,
            Library(activityPackageName, listOf(activityArtifact)),
            Library(roomPackageName, listOf(roomArtifact))
        )

        verify(trackerDao).loadArtifacts()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun reflectDataUpdate() = runBlockingTest {
        val updateActivityArtifact =
            activityArtifact.copy(version = Artifact.Version("2.1.1", "2.2.1-beta02"))
        val updateRoomArtifact =
            roomArtifact.copy(version = Artifact.Version("2.2.2", "2.3.1-alpha01"))
        val flow = flow {
            emit(listOf(activityArtifact, roomArtifact))
            delay(2000)
            emit(listOf(updateActivityArtifact, updateRoomArtifact))
        }

        `when`(trackerDao.loadArtifacts()).thenReturn(flow)

        val libraries = mutableListOf<List<Library>>()

        val job = launch {
            repository.loadLibraries().toList(libraries)
        }

        advanceTimeBy(2000)

        assertLibraries(
            libraries[0],
            Library(activityPackageName, listOf(activityArtifact)),
            Library(roomPackageName, listOf(roomArtifact))
        )

        assertLibraries(
            libraries[1],
            Library(activityPackageName, listOf(updateActivityArtifact)),
            Library(roomPackageName, listOf(updateRoomArtifact))
        )

        job.cancel()
    }

    @Test
    fun artifactsToLibrary() {
        val version = Artifact.Version("1", "1")

        val artifacts = listOf(
            Artifact("artifact1", version, "lib1"),
            Artifact("artifact2", version, "lib2")
        )

        val expected = listOf(
            Library("lib1", listOf(Artifact("artifact1", version, "lib1"))),
            Library("lib2", listOf(Artifact("artifact2", version, "lib2")))
        )

        assertThat(repository.fromArtifactsToLibrary(artifacts), `is`(equalTo(expected)))
    }

    private fun assertLibraries(actual: List<Library>, vararg libraries: Library) {
        assertThat(actual, `is`(equalTo(libraries.toList())))
    }

    private suspend fun verifyGetLibrary(libraryName: String) {
        verify(apiService).getLibrary("androidx/$libraryName")
    }

}