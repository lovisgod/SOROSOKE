package com.kodehauz.radiobasar.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.github.loadingview.LoadingDialog
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.kodehauz.radiobasar.R
import com.kodehauz.radiobasar.utils.OnClearFromRecentService
import com.kodehauz.radiobasar.utils.Playable
import com.kodehauz.radiobasar.utils.cancalNotifications
import com.kodehauz.radiobasar.utils.sendNotification
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var toolbar: MaterialToolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appBarLayout = findViewById(R.id.tool_bar_layout)
        toolbar = findViewById(R.id.tool_bar)
        navController = Navigation.findNavController(this, R.id.app_nav_host_fragment)
        NavigationUI.setupWithNavController(toolbar, navController)


    }
}
