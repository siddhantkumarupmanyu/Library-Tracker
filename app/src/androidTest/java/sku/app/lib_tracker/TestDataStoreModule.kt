package sku.app.lib_tracker

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import sku.app.lib_tracker.datastore.*
import java.io.File
import javax.inject.Singleton


//@TestInstallIn(
//    components = [SingletonComponent::class],
//    replaces = [DataStoreModule::class]
//)
@Module
@InstallIn(SingletonComponent::class)
object TestDataStoreModule {


    @Singleton
    @Provides
    fun providedDataStore(
        app: Application,
        scope: TestDataStoreScope
    ): DataStore<TrackerPreferences> {
        return DataStoreFactory.create(
            serializer = TrackerPrefsSerializer,
            produceFile = { app.tempDataStoreFile(DATA_STORE_FILE_NAME) },
            scope = scope.scope
        )
    }


    @Provides
    fun providesDataStoreHelper(dataStore: DataStore<TrackerPreferences>): DataStoreHelper {
        return ProtoBuffHelper(dataStore)
    }

}

fun Context.tempDataStoreFile(fileName: String): File =
    File(applicationContext.filesDir, "tempdatastore/$fileName")

data class TestDataStoreScope(val scope: CoroutineScope)
