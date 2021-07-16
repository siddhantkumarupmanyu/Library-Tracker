package sku.app.lib_tracker.datastore

import androidx.datastore.core.DataStore

const val DATA_STORE_FILE_NAME = "tracker_prefs.pb"

// lack of better name
class PreferencesHelper(dataStore: DataStore<TrackerPreferences>) {
    suspend fun haveFetchedToday(): Boolean {
        return false
    }


}

// i tried to test this class a different way than dao class
// I do not want DateUtils to implement a interface which we can mock
// just for testing this class
// it would be more oop style testing though
// but unnecessary complexity in code.
// right now we have a bit complexity in test but it's ok