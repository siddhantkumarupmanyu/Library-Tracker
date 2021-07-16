package sku.app.lib_tracker.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import sku.app.lib_tracker.datastore.*
import java.io.File
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataStoreModule {


    @Singleton
    @Provides
    fun providedDataStore(app: Application): DataStore<TrackerPreferences> {
        return DataStoreFactory.create(
            serializer = TrackerPrefsSerializer,
            produceFile = { app.dataStoreFile(DATA_STORE_FILE_NAME) }
        )
    }


    @Provides
    fun providesDataStoreHelper(dataStore: DataStore<TrackerPreferences>): DataStoreHelper {
        return ProtoBuffHelper(dataStore)
    }

}

fun Context.dataStoreFile(fileName: String): File =
    File(applicationContext.filesDir, "datastore/$fileName")