package sku.app.lib_tracker.datastore

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.first
import sku.app.lib_tracker.vo.CustomDate

const val DATA_STORE_FILE_NAME = "tracker_prefs.pb"

class ProtoBuffHelper(private val dataStore: DataStore<TrackerPreferences>) : DataStoreHelper {

    /**
     * @param date takes today's date as default
     */
    override suspend fun shouldFetch(date: CustomDate): Boolean {
        return date > getLastFetchDate()
    }

    /**
     * @return lastFetchDate from datastore, if not found defaults to CustomDate().yesterday
     * @see TrackerPrefsSerializer.defaultValue
     */
    override suspend fun getLastFetchDate(): CustomDate {
        val dateString = dataStore.data.first().lastFetchDate
        return CustomDate.parse(dateString)
    }

    /**
     * @param date takes today's date as default
     */
    override suspend fun saveLastFetchDate(date: CustomDate) {
        dataStore.updateData { prefs: TrackerPreferences ->
            prefs.toBuilder()
                .setLastFetchDate(date.toString())
                .build()
        }
    }

    override suspend fun showNotification(): Boolean {
        return dataStore.data.first().showNotification
    }

    override suspend fun setShowNotification(show: Boolean) {
        dataStore.updateData { prefs: TrackerPreferences ->
            prefs.toBuilder()
                .setShowNotification(show)
                .build()
        }
    }


}