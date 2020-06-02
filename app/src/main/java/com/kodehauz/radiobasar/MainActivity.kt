package com.kodehauz.radiobasar

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
import com.github.loadingview.LoadingDialog
import com.kodehauz.radiobasar.utils.OnClearFromRecentService
import com.kodehauz.radiobasar.utils.Playable
import com.kodehauz.radiobasar.utils.cancalNotifications
import com.kodehauz.radiobasar.utils.sendNotification
import java.io.IOException


class MainActivity : AppCompatActivity(), Playable {
    private lateinit var player: MediaPlayer
    private var playing: Boolean = false
    private lateinit var playButton: Button
    val ACTION_PLAY = "PLAY"
    val ACTION_PAUSE = "PAUSE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createChannel(
            getString(R.string.radio_notification_channel_id),
            getString(R.string.egg_notification_channel_name)
        )
        registerReceiver(broadcastReceiver, IntentFilter("TRACK TRACK"))
        startService(Intent(baseContext, OnClearFromRecentService::class.java))
        initializeMediaPlayer()
        showNotification()
        playButton = findViewById(R.id.play)
        playButton.setOnClickListener {
            if (playing) {
                pausePlaying()
                playing = false
                println(playing)
            } else {
                startPlaying()
                playing = true
                println(playing)
            }
        }
    }

    private fun startPlaying() {
        player.start()
        playButton.text = "Stop"
    }

    private fun pausePlaying() {
        if (player.isPlaying) {
            player.pause()
            playButton.text  = "Play"
        }
    }

    private fun initializeMediaPlayer() {
        val dailog = LoadingDialog.get(this)

        dailog.show()
        player = MediaPlayer()
        val url = "http://159.65.180.178:8550/;live.mp3"
        try {
           player.setDataSource(url)
           player.prepareAsync()
            player.setOnBufferingUpdateListener { _, percent ->  println(percent) }
            player.setOnPreparedListener {
                println("this gets here")
                dailog.hide()
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onPlay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = this@MainActivity.getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.sendNotification(
                "hello",
                this@MainActivity,
                R.drawable.ic_pause_black_24dp
            )
        }
        startPlaying()
        playing = true
    }

    override fun onTrackPause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = this@MainActivity.getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.sendNotification(
                "hello",
                this@MainActivity,
                R.drawable.ic_play_arrow_black_24dp
            )
        }

       pausePlaying()
       playing = false
    }

    override fun onPause() {
        super.onPause()
        if (player.isPlaying) {

        }
    }


    override fun onStop() {
        super.onStop()
        if (player.isPlaying) {

        }
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.cancalNotifications()
        }
        unregisterReceiver(broadcastReceiver)
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Listening to radio"

            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.createNotificationChannel(notificationChannel)
        }


    }


    private fun showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.sendNotification(
                "Listen to life",
                this,
                R.drawable.ic_play_arrow_black_24dp
            )
        }
    }


    var broadcastReceiver: BroadcastReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when(intent.getStringExtra("playpause")) {
                    ACTION_PAUSE -> {
                        onTrackPause()
                    }

                    ACTION_PLAY -> {
                        if (playing) {
                            onTrackPause()
                        } else {
                            onPlay()
                        }

                    }
                }
            }
        }


}
