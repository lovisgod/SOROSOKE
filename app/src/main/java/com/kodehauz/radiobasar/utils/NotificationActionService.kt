package com.kodehauz.radiobasar.utils

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationActionService: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
     context!!.sendBroadcast(Intent("TRACK TRACK").putExtra("playpause", intent!!.action))
    }
}