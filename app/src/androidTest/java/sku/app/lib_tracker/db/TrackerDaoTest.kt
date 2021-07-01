package sku.app.lib_tracker.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import sku.app.lib_tracker.utils.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import sku.app.lib_tracker.utils.TestUtils

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class TrackerDaoTest : DbTest() {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val trackerDao
        get() = db.trackerDao()

    @Test
    fun insertAndLoadArtifacts() = mainCoroutineRule.runBlockingTest {

        val activityArtifact = TestUtils.createArtifact("activity-compose", "androidx.activity")
        val roomArtifact = TestUtils.createArtifact("room", "androidx.room")

        val artifacts = listOf(
            activityArtifact,
            roomArtifact
        )

        trackerDao.insertArtifacts(artifacts)

        val results = trackerDao.loadArtifacts().first()

        assertThat(results[0], `is`(activityArtifact))
        assertThat(results[1], `is`(roomArtifact))
    }

    @Test
    fun updateArtifacts() {

    }

}