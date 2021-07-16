package sku.app.lib_tracker.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import junit.framework.Assert.assertFalse
import kotlinx.coroutines.*
import org.junit.*
import org.junit.rules.TemporaryFolder
import java.io.File

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

    // TODO:

    @After
    fun teardown() {
        dataStoreScope.cancel()
    }

    @Test
    fun haveFetchedToday_Default() = runBlocking {
        assertFalse(helper.haveFetchedToday())
    }

    @Test
    @Ignore
    fun haveFetchedToday() {
        createProtoFileWith()

    }

    private fun createProtoFileWith() {
        // todo:
        // dataStore.updateData { prefs: TrackerPreferences ->
        //     prefs.toBuilder().setLastFetchDate()
        // }
    }

    @Test
    @Ignore
    fun showNotification() {
        // case with default value

        // case without default value
    }

}