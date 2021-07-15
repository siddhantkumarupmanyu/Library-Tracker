package sku.app.lib_tracker

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import sku.app.lib_tracker.db.TrackerDao
import sku.app.lib_tracker.db.TrackerDb
import sku.app.lib_tracker.di.DbModule
import javax.inject.Singleton


@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DbModule::class]
)
object TestDbModule {

    @Singleton
    @Provides
    fun providesDb(app: Application): TrackerDb {
        return Room.inMemoryDatabaseBuilder(
            app,
            TrackerDb::class.java
        ).build()
    }

    @Singleton
    @Provides
    fun providesTrackerDb(db: TrackerDb): TrackerDao {
        return db.trackerDao()
    }

}