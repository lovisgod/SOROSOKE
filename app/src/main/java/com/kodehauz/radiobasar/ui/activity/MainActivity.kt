package com.kodehauz.radiobasar.ui.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.github.loadingview.LoadingDialog
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.kodehauz.radiobasar.R
import com.kodehauz.radiobasar.utils.*
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var toolbar: Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appBarLayout = findViewById(R.id.tool_bar_layout)
        toolbar = findViewById(R.id.tool_bar)
        toolbar.inflateMenu(R.menu.app_menu)
        navController = Navigation.findNavController(this, R.id.app_nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration.Builder(navController.graph).build()
        NavigationUI.setupWithNavController(toolbar, navController)

        volumeControlStream = AudioManager.STREAM_MUSIC
        toolbar.setOnMenuItemClickListener {
            return@setOnMenuItemClickListener when(it.itemId){
                R.id.contactFragment -> {
                    navController.navigate(R.id.contactFragment)
                    true
                }
                R.id.aboutFragment -> {
                    navController.navigate(R.id.aboutFragment)
                    true
                }
                else -> false
            }
        }

    }
}