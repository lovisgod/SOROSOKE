package com.kodehauz.radiobasar.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.kodehauz.radiobasar.ui.activity.MainActivity
import com.kodehauz.radiobasar.R
import java.lang.Exception

// Notification ID.
private val NOTIFICATION_ID = 2
private val REQUEST_CODE = 0
private val FLAGS = 0
private val ACTION_PLAY = "PLAY"
private val ACTION_PAUSE = "PAUSE"


// TODO: Step 1.1 extension function to send messages (GIVEN)
/**
 * Builds and delivers the notification.
 *
 * @param context, activity context.
 */

// this is extension function from the notification class that gives freedom to perform different operation with
// the notification
fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context, playBtn:Int) {
    try {
        val mediaSession = MediaSessionCompat(applicationContext, "tag")

        // handle play action
        val intentPlay = Intent(applicationContext, NotificationActionService::class.java)
            .setAction("PLAY")
        val pendingIntentPlay  = PendingIntent.getBroadcast(
            applicationContext,
            REQUEST_CODE,
            intentPlay,
            PendingIntent.FLAG_UPDATE_CURRENT)


        // Create the content intent for the notification, which launches
        // this activity
        //  create intent
        val contentIntent = Intent(applicationContext, MainActivity::class.java)
        //  create PendingIntent
        val contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // add style

        val radioImage =  BitmapFactory.decodeResource(
            applicationContext.resources,
            R.drawable.man_
        )

        //  get an instance of NotificationCompat.Builder
        // Build the notification
        val builder = NotificationCompat.Builder(
            applicationContext, applicationContext.resources.getString(R.string.radio_notification_channel_id)
        )
            .setSmallIcon(R.drawable.ic_basar_icon)
            .setLargeIcon(radioImage)
            .setContentTitle(applicationContext.
            resources.getString(R.string.notification_title))
            .setContentText(messageBody)
            .setContentIntent(contentPendingIntent)
            .setOnlyAlertOnce(true)
            .addAction(
                playBtn,
                applicationContext.getString(R.string.play),
                pendingIntentPlay
            )
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0)
                .setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_LOW)

        // add play action



        // nptify gets two parameters the notification id and the builder
        notify(NOTIFICATION_ID, builder.build())
    } catch (e:Exception) {
        println("this is the error ${e.localizedMessage}")
    }
}

// Cancel all notifications

fun NotificationManager.cancalNotifications() {
    cancelAll()
}