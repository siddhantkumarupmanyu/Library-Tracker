package sku.app.lib_tracker.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import sku.app.lib_tracker.R

class SpikeWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val notificationManager =
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val notificationBuilder by lazy {
        createNotificationBuilder()
    }

    override suspend fun doWork(): Result {

        setForeground(createForegroundInfo())

        Log.d(this::class.simpleName, "Fetching From Network")

        delay(20000)

        updateNotification("Parsing")

        delay(20000)

        Log.d(this::class.simpleName, "Parsing Results")

        updateNotification("Done")

        delay(2000)

        return Result.success()
    }

    private fun updateNotification(contentText: String) {
        notificationBuilder.setContentTitle(contentText)
        val notificationId = 1
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun createForegroundInfo(): ForegroundInfo {
        // move this Id to constants class
        val notificationId = 1
        return ForegroundInfo(notificationId, createNotification())
    }

    private fun createNotification(): Notification = notificationBuilder.build()

    private fun createNotificationBuilder(): NotificationCompat.Builder {
        // move these stings to string resources
        val channelId = "SpikeWorkManager"
        val title = "Spike Worker Notification"
        val cancel = "Cancel"
        val channelName = "Spike Work Manager"

        val cancelIntent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(channelId, channelName)
        }

        return NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Fetching")
            .setTicker(title)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setOngoing(true)
            .setShowWhen(false)
            .setProgress(0,0, true)
            .addAction(R.drawable.ic_launcher_background, cancel, cancelIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String) {
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }

}