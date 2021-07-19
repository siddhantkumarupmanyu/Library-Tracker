package sku.app.lib_tracker.work.fetch_worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import sku.app.lib_tracker.R
import sku.app.lib_tracker.repository.TrackerRepository

class FetchWorker(
    private val repository: TrackerRepository,
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val notificationManager =
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {

        setForeground(createForegroundInfo())

        repository.fetchAndSave()

        // TODO: show  notification only when showNotification is enabled

        return Result.success()
    }

    private fun createForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(NOTIFICATION_ID, createNotification())
    }

    private fun createNotification(): Notification {
        val channelId = getString(R.string.notification_channel_id)
        val title = getString(R.string.notification_title)
        val cancel = getString(R.string.notification_cancel)
        val channelName = getString(R.string.notification_channel_name)

        val cancelIntent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(channelId, channelName)
        }

        return NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setTicker(title)
            .setOngoing(true)
            .setShowWhen(false)
            .setProgress(0, 0, true)
            .addAction(android.R.drawable.ic_delete, cancel, cancelIntent)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String) {
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)

        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val NOTIFICATION_ID = 1
    }

    private fun getString(@StringRes id: Int) = applicationContext.getString(id)

}