package io.omniedge

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import io.omniedge.n2n.N2NNotificationProvider
import io.omniedge.n2n.N2NNotificationProvider.Companion.notificationId
import io.omniedge.n2n.N2NNotificationProvider.Companion.notificationManager
import io.omniedge.ui.activity.DeviceListActivity

class N2NNotificationProviderImpl : N2NNotificationProvider {
    override fun addNotification(context: Context) {
        context.run {
            val mainIntent = Intent(this, DeviceListActivity::class.java)
            val mainPendingIntent = PendingIntent.getActivity(
                this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            val builder = NotificationCompat
                .Builder(
                    this,
                    context.getString(R.string.notification_channel_id_default)
                )
                .setSmallIcon(R.drawable.ic_omniedge)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        resources,
                        R.drawable.ic_state_supernode_diconnect
                    )
                )
                .setColor(ContextCompat.getColor(this, R.color.colorSupernodeDisconnect))
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notify_disconnect))
                .setFullScreenIntent(null, false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(false)
                .setContentIntent(mainPendingIntent)

            val notification = builder.build()
            notification.flags = notification.flags or Notification.FLAG_NO_CLEAR
            notificationManager.notify(notificationId, notification)
        }


    }

    override fun updateNotification(context: Context) {
        context.run {
            val mainIntent: Intent = Intent(
                this,
                DeviceListActivity::class.java
            )
            val mainPendingIntent = PendingIntent.getActivity(
                this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )

            val builder = NotificationCompat.Builder(
                this,
                getString(R.string.notification_channel_id_default)
            )
                .setSmallIcon(R.drawable.ic_omniedge)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_omniedge))
                .setColor(ContextCompat.getColor(this, R.color.colorWhite))
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notify_reconnected))
                .setFullScreenIntent(null, false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setContentIntent(mainPendingIntent)

            val notification = builder.build()

            notificationManager.notify(notificationId, notification)
        }
    }
}