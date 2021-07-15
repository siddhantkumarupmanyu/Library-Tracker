package sku.app.lib_tracker

import android.app.Application
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import sku.app.lib_tracker.work.TrackerWorkFactory
import javax.inject.Inject

@HiltAndroidApp
class TrackerApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var trackerWorkFactory: TrackerWorkFactory

    @Inject
    lateinit var workConfiguration: Configuration

    override fun getWorkManagerConfiguration(): Configuration {
        return workConfiguration
    }

}