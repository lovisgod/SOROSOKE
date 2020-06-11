package com.kodehauz.radiobasar.utils

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.session.MediaButtonReceiver

class OnClearFromRecentService: Service() {
    private val mediaSession = MediaSessionCompat(this, "RADIOBASSAR")
    override fun onBind(intent: Intent?): IBinder? {
        return  null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaSession, intent)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopSelf()
    }
}