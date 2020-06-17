package com.kodehauz.radiobasar.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi
import com.kodehauz.radiobasar.models.AppEvent
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit

class AppAudioManger(val mediaPlayer: MediaPlayer, audioManger: AudioManager) {

    var playbackDelayed = false
    fun setVolume(audioManger: AudioManager, value:Int) {
        audioManger.setStreamVolume(AudioManager.STREAM_MUSIC, value, AudioManager.FLAG_PLAY_SOUND)
    }

    fun setVolumeDown(audioManger: AudioManager) {
        audioManger.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun muteVolume(audioManger: AudioManager) {
        audioManger.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun unMuteVolume(audioManger: AudioManager) {
        audioManger.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_TOGGLE_MUTE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
    }


    fun getVolume(audioManger: AudioManager): Int {
        return audioManger.getStreamVolume(AudioManager.STREAM_MUSIC)
    }

    fun getMaxVolume(audioManger: AudioManager):Int {
        return  audioManger.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    }



    private val handler = Handler()
    private val afChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                // Permanent loss of audio focus
                // Pause playback immediately
                mediaPlayer.pause()
                EventBus.getDefault().post(AppEvent(event = "pause"))
                // Wait 30 seconds before stopping playback
                handler.postDelayed(delayedStopRunnable, TimeUnit.SECONDS.toMillis(30))
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                mediaPlayer.pause()
                EventBus.getDefault().post(AppEvent(event = "pause"))
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                // Lower the volume, keep playing
                setVolumeDown(audioManger)
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                // Your app has been granted audio focus again
                if (playbackDelayed) {
                        playbackDelayed = false
                    }
                mediaPlayer.start()
                // Raise volume to normal, restart playback if necessary
            }
        }
    }

    private var delayedStopRunnable = Runnable {
       mediaPlayer.stop()
    }

    fun getMediaController(context: Context): MediaControllerCompat {
        return MediaControllerCompat(context, MediaSessionCompat(context, "RADIOBASSAR").sessionToken)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun requestFocus (audioManger: AudioManager) {
       val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
            setAudioAttributes(AudioAttributes.Builder().run {
                setUsage(AudioAttributes.USAGE_MEDIA)
                setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                build()
            })
            setAcceptsDelayedFocusGain(true)
            setOnAudioFocusChangeListener(afChangeListener, handler)
            build()
        }


        val res = audioManger.requestAudioFocus(focusRequest)
       when (res) {
            AudioManager.AUDIOFOCUS_REQUEST_FAILED -> {
                mediaPlayer.pause()
                EventBus.getDefault().post(AppEvent(event = "pause"))
            }
            AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
               mediaPlayer.start()

            }
            AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> {
                playbackDelayed = true

            }
            else -> {
                // do nothing
            }
        }

    }

    fun requestFocusLowerVersion (audioManger: AudioManager) {

        val res = audioManger.requestAudioFocus( afChangeListener,
            // Use the music stream.
            AudioManager.STREAM_MUSIC,
            // Request permanent focus.
            AudioManager.AUDIOFOCUS_GAIN)
        when (res) {
            AudioManager.AUDIOFOCUS_REQUEST_FAILED -> {
                mediaPlayer.pause()
                EventBus.getDefault().post(AppEvent(event = "pause"))
            }
            AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                mediaPlayer.start()

            }
            AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> {
                playbackDelayed = true

            }
            else -> {
                // do nothing
            }
        }

    }



}