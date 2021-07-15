package sku.app.lib_tracker.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import sku.app.lib_tracker.db.TrackerDao
import sku.app.lib_tracker.db.TrackerDb
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DbModule {
    @Singleton
    @Provides
    fun providesDb(app: Application): TrackerDb {
        return Room.databaseBuilder(app, TrackerDb::class.java, "tracker.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun providesTrackerDao(db: TrackerDb): TrackerDao {
        return db.trackerDao()
    }
}