package sku.app.lib_tracker.datastore

import sku.app.lib_tracker.vo.CustomDate

interface DataStoreHelper {
    /**
     * @param date takes today's date as default
     */
    suspend fun shouldFetch(date: CustomDate = CustomDate()): Boolean

    /**
     * @return lastFetchDate from datastore, if not found defaults to CustomDate().yesterday
     * @see TrackerPrefsSerializer.defaultValue
     */
    suspend fun getLastFetchDate(): CustomDate

    /**
     * @param date takes today's date as default
     */
    suspend fun saveLastFetchDate(date: CustomDate = CustomDate())

    suspend fun showNotification(): Boolean

    suspend fun setShowNotification(show: Boolean)
}