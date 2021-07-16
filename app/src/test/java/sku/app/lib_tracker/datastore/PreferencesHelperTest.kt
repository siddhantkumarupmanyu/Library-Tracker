package sku.app.lib_tracker.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import junit.framework.Assert.assertTrue
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import sku.app.lib_tracker.vo.CustomDate
import java.io.File
import java.time.Month

class PreferencesHelperTest {

    @Rule
    @JvmField
    val tempFolder = TemporaryFolder()

    private val dataStoreScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private lateinit var dataStore: DataStore<TrackerPreferences>

    private lateinit var helper: PreferencesHelper

    @Before
    fun setup() {
        dataStore = DataStoreFactory.create(
            serializer = TrackerPrefsSerializer,
            produceFile = { File(tempFolder.root, "datastore/$DATA_STORE_FILE_NAME") },
            scope = dataStoreScope
        )

        helper = PreferencesHelper(dataStore)
    }

    @After
    fun teardown() {
        dataStoreScope.cancel()
    }

    @Test
    fun saveAndGetLastFetchDate() = runBlocking {
        val date1 = CustomDate(12, Month.JANUARY, 2021)

        helper.saveLastFetchDate(date1)

        assertThat(helper.getLastFetchDate(), `is`(equalTo(date1)))
    }

    @Test
    fun shouldFetch() = runBlocking {
        val date1 = CustomDate(12, Month.JANUARY, 2021)
        val date2 = CustomDate(13, Month.JANUARY, 2021)

        helper.saveLastFetchDate(date1)

        assertTrue(helper.shouldFetch(date2))
    }

    @Test
    fun shouldNotFetch() = runBlocking {
        helper.saveLastFetchDate()
        assertFalse(helper.shouldFetch())
    }

    @Test
    fun getLastFetchDate() = runBlocking {
        val yesterday = CustomDate().yesterday

        assertThat(helper.getLastFetchDate(), `is`(equalTo(yesterday)))
    }

    @Test
    fun shouldFetchWhenNoValueInDatastore() = runBlocking {
        assertTrue("should fetch on default", helper.shouldFetch())
    }

    @Test
    fun showNotification() = runBlocking {
        helper.setShowNotification(true)
        assertThat(helper.showNotification(), `is`(equalTo(true)))

        helper.setShowNotification(false)
        assertThat(helper.showNotification(), `is`(equalTo(false)))
    }

    @Test
    fun showNotificationWhenNoValueInDatastore() = runBlocking {
        assertTrue("should show on default", helper.showNotification())
    }


}

// integration test since it also tests TrackerPrefsSerializer