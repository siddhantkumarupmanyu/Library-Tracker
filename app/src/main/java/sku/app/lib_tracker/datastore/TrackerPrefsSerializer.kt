package sku.app.lib_tracker.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream

object TrackerPrefsSerializer : Serializer<TrackerPreferences> {
    override val defaultValue: TrackerPreferences
        get() = TrackerPreferences.getDefaultInstance()

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


}