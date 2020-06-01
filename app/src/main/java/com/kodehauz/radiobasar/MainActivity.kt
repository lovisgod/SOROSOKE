package com.kodehauz.radiobasar

import android.app.Dialog
import android.app.ProgressDialog
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.loadingview.LoadingDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private lateinit var player: MediaPlayer
    private var playing: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeMediaPlayer()
        val playButton: Button = findViewById(R.id.play)
        playButton.setOnClickListener {
            if (playing) {
                pausePlaying()
                playing = false
                println(playing)
                playButton.text  = "Play"
            } else {
                startPlaying()
                playing = true
                println(playing)
                playButton.text = "Stop"
            }
        }
    }

    private fun startPlaying() {
        player.start()
    }

    private fun pausePlaying() {
        if (player.isPlaying) {
            player.pause()
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


}
