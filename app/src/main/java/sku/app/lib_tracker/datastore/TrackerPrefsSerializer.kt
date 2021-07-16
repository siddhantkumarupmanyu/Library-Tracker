package sku.app.lib_tracker.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import sku.app.lib_tracker.vo.CustomDate
import java.io.InputStream
import java.io.OutputStream

object TrackerPrefsSerializer : Serializer<TrackerPreferences> {

    /**
     * Builds default TrackerPreference with default date as CustomDate().yesterday and showNotification as true
     */
    override val defaultValue: TrackerPreferences
        get() = buildDefaultTrackerPrefs()

    override suspend fun readFrom(input: InputStream): TrackerPreferences {
        try {
            return TrackerPreferences.parseFrom(input)
        } catch (exception: CorruptionException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: TrackerPreferences, output: OutputStream) {
        t.writeTo(output)
    }

    private fun buildDefaultTrackerPrefs(): TrackerPreferences {
        return TrackerPreferences.newBuilder()
            .setLastFetchDate(CustomDate().yesterday.toString())
            .setShowNotification(true)
            .build()
    }


}