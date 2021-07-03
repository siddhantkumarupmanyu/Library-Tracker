package sku.app.lib_tracker

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import sku.app.lib_tracker.workers.TrackerWorkFactory
import javax.inject.Inject

@HiltAndroidApp
class TrackerApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var trackerWorkFactory: TrackerWorkFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(trackerWorkFactory)
            .build()
    }

}