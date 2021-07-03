package sku.app.lib_tracker.workers

import androidx.work.DelegatingWorkerFactory
import sku.app.lib_tracker.repository.TrackerRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackerWorkFactory @Inject constructor(
    private val repository: TrackerRepository
) :
    DelegatingWorkerFactory() {

        init {
            addFactory(FetchWorkerFactory(repository))
        }

}