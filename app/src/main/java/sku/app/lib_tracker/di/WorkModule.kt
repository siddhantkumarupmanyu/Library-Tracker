package sku.app.lib_tracker.di

import android.util.Log
import androidx.work.Configuration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import sku.app.lib_tracker.work.TrackerWorkFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object WorkModule {

    @Singleton
    @Provides
    fun providesWorkConfiguration(trackerWorkFactory: TrackerWorkFactory): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(trackerWorkFactory)
            .build()
    }

}