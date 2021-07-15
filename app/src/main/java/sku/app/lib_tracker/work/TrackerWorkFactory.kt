package sku.app.lib_tracker.work

import androidx.work.DelegatingWorkerFactory
import sku.app.lib_tracker.repository.TrackerRepository
import sku.app.lib_tracker.work.fetch_worker.FetchWorkerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackerWorkFactory @Inject constructor(
    private val repository: TrackerRepository
) : DelegatingWorkerFactory() {

    init {
        addFactory(FetchWorkerFactory(repository))
    }

}

// ref:
// https://medium.com/androiddevelopers/customizing-workmanager-fundamentals-fdaa17c46dd2
// https://medium.com/androiddevelopers/customizing-workmanager-with-dagger-1029688c0978