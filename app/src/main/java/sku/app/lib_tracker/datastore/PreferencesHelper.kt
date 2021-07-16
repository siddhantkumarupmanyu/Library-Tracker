package sku.app.lib_tracker.datastore

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.first
import sku.app.lib_tracker.vo.CustomDate

const val DATA_STORE_FILE_NAME = "tracker_prefs.pb"

// lack of better name
class PreferencesHelper(private val dataStore: DataStore<TrackerPreferences>) {

    /**
     * @param date takes today's date as default
     */
    suspend fun shouldFetch(date: CustomDate = CustomDate()): Boolean {
        return date > getLastFetchDate()
    }

    /**
     * @return lastFetchDate from datastore, if not found defaults to CustomDate().yesterday
     * @see TrackerPrefsSerializer.defaultValue
     */
    suspend fun getLastFetchDate(): CustomDate {
        val dateString = dataStore.data.first().lastFetchDate
        return CustomDate.parse(dateString)
    }

    /**
     * @param date takes today's date as default
     */
    suspend fun saveLastFetchDate(date: CustomDate = CustomDate()) {
        dataStore.updateData { prefs: TrackerPreferences ->
            prefs.toBuilder()
                .setLastFetchDate(date.toString())
                .build()
        }
    }

    suspend fun showNotification(): Boolean {
        return dataStore.data.first().showNotification
    }

    suspend fun setShowNotification(show: Boolean) {
        dataStore.updateData { prefs: TrackerPreferences ->
            prefs.toBuilder()
                .setShowNotification(show)
                .build()
        }
    }


}