package com.kodehauz.radiobasar.ui.fragment

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.github.loadingview.LoadingDialog
import com.kodehauz.radiobasar.R
import com.kodehauz.radiobasar.databinding.FragmentPlayerBinding
import com.kodehauz.radiobasar.models.AppEvent
import com.kodehauz.radiobasar.models.ErrorEvent
import com.kodehauz.radiobasar.models.MediaEvent
import com.kodehauz.radiobasar.ui.bottomSheet.CommentBottomSheet
import com.kodehauz.radiobasar.utils.*
import com.kodehauz.radiobasar.viewmodel.AppViewModel
import com.pixplicity.easyprefs.library.Prefs
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 * Use the [PlayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlayerFragment : Fragment(),  Playable {
    private lateinit var binding: FragmentPlayerBinding
    private lateinit var navController: NavController
    private lateinit var player: MediaPlayer
    private lateinit var appAudioManager: AppAudioManger
    private lateinit var audioManager: AudioManager
    private var playing: Boolean = false
    private var muted: Boolean = false
    private lateinit var playButton: ImageView
    val ACTION_PLAY = "PLAY"
    val ACTION_PAUSE = "PAUSE"
    var initialProgressValue = 0
    val playerManager = PlayerManager()
    private lateinit var dataDialog: AlertDialog
    private lateinit var mShare : Button


    private val viewModel: AppViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(activity, AppViewModel.Factory(activity.application))
            .get(AppViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        player = playerManager
        initializeMediaPlayer()
        audioManager  = this.requireContext().getSystemService(AUDIO_SERVICE) as AudioManager
        navController = Navigation.findNavController(this.requireActivity(), R.id.app_nav_host_fragment)
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_player, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        appAudioManager = AppAudioManger(player, audioManager)
        binding.sound.setColorFilter(this.requireContext().resources.getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP)
        binding.volume.max = appAudioManager.getMaxVolume(audioManager)
        initialProgressValue = binding.volume.max
        binding.volume.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                appAudioManager.setVolume(audioManager, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        createChannel(
            getString(R.string.radio_notification_channel_id),
            getString(R.string.egg_notification_channel_name)
        )
        this.requireActivity().registerReceiver(broadcastReceiver, IntentFilter("TRACK TRACK"))
        this.requireActivity().startService(Intent(this.requireActivity().baseContext, OnClearFromRecentService::class.java))
        playButton = binding.play
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

        binding.shareBtn.setOnClickListener {
            val myIntent = Intent(Intent.ACTION_SEND)
            myIntent.type = "text/plain"
            myIntent.putExtra(Intent.EXTRA_SUBJECT, "RADIOBASSAR")
            val shareMessage = "Get inspired daily, Listen to RadioBassar"
            myIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(myIntent,"Share via"))
        }

        binding.sound.setOnClickListener {
            if ( muted ) {
                appAudioManager.unMuteVolume(audioManager)
                muted = false
                println(muted)
                binding.sound.setColorFilter(this.requireContext().resources.getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP)
            }

            else {
                appAudioManager.muteVolume(audioManager)
                muted = true
                println(muted)
                binding.sound.setColorFilter(this.requireContext().resources.getColor(R.color.redcolor), PorterDuff.Mode.SRC_ATOP);
            }
        }

        binding.commentBtn.setOnClickListener{
            val firstTimeComment = Prefs.getBoolean("first_time_comment", false)
            if (!firstTimeComment){
                println(firstTimeComment)
                dataDialog = Dialog().displayInputContactDialog(this.requireContext(), viewModel)!!
                dataDialog.show()
            } else {
                val bottomSheet = CommentBottomSheet.newInstance(R.layout.comment_layout, viewModel)
                bottomSheet?.show(this.requireActivity().supportFragmentManager.beginTransaction(), "dialog_comment")
            }

        }

        viewModel._installCount.observe(viewLifecycleOwner, Observer {
            binding.numbers.text = it.toString()
        })

        if (player.isPlaying) {
            playButton.setImageDrawable(this.requireContext().resources.getDrawable(R.drawable.ic_pause_stop))
            showNotification(R.drawable.ic_pause_black_24dp)
            playing = true
        }

       return binding.root
    }

    private fun startPlaying() {
        playButton.setImageDrawable(this.requireContext().resources.getDrawable(R.drawable.ic_pause_stop))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appAudioManager.requestFocus(audioManager)
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            appAudioManager.requestFocusLowerVersion(audioManager)
        }
//        player.start()
        playing = true
        showNotification(R.drawable.ic_pause_black_24dp)
    }

    private fun pausePlaying() {
        if (player.isPlaying) {
            playButton.setImageDrawable(this.requireContext().resources.getDrawable(R.drawable.ic_buttonplay))
            player.pause()
            playing = false
            showNotification(R.drawable.ic_play_arrow_black_24dp)
        }
    }

    private fun initializeMediaPlayer() {
        if (!player.isPlaying) {
            val dailog = LoadingDialog.get(this.requireActivity())

            dailog.show()
            val url = "http://159.65.180.178:8550/;live.mp3"
//        val url = "https://s25.myradiostream.com/15102/listen.mp3"
            try {
                player.setDataSource(url)
                player.prepareAsync()
                player.setOnBufferingUpdateListener { _, percent ->  println(percent) }
                player.setOnPreparedListener {
                    dailog.hide()
                }
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            playing = true
        }

    }

    override fun onPlay() {

        startPlaying()
        playing = true
        showNotification(R.drawable.ic_pause_black_24dp)
    }

    override fun onTrackPause() {
        pausePlaying()
        playing = false
        showNotification(R.drawable.ic_play_arrow_black_24dp)
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
        // unregister the event listener
        EventBus.getDefault().unregister(this)
    }

    override fun onStart() {
        super.onStart()
        // registers the event listener
        EventBus.getDefault().register(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = this.requireContext().getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.cancalNotifications()
        }
        this.requireActivity().unregisterReceiver(broadcastReceiver)
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

            val notificationManager = this.requireContext().getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.createNotificationChannel(notificationChannel)
        }


    }


    private fun showNotification(playBtn:Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = this.requireContext().getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.sendNotification(
                "Listen to life",
                this.requireContext(),
                playBtn
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

    companion object {
        @JvmStatic
        fun newInstance() =
            PlayerFragment()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAppEvent(event: AppEvent) {

        when(event.event) {
            "pause" -> {
                playButton.setImageDrawable(this.requireContext().resources.getDrawable(R.drawable.ic_buttonplay))
                showNotification(R.drawable.ic_play_arrow_black_24dp)
            }
            "dataCaptureSuccess" -> {
                Dialog().makeSnack(binding.commentBtn, "Data capture successful", this.requireContext())
                Prefs.putBoolean("first_time_comment", true)
                dataDialog.dismiss()
                val bottomSheet = CommentBottomSheet.newInstance(R.layout.comment_layout, viewModel)
                bottomSheet?.show(this.requireActivity().supportFragmentManager.beginTransaction(), "dialog_comment")
            }

            "commentSubmitSuccess" -> {
                println("success")
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onErrorEvent(event: ErrorEvent) {

        when(event.event) {
            "dataCaptureError", "commentSaveError", "commentListError" -> {
                Dialog().makeSnack(binding.commentBtn, event.message, this.requireContext())
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMediaEvent(event: MediaEvent) {
        when (event.code) {

            KeyEvent.KEYCODE_HEADSETHOOK -> {
                if (player.isPlaying) {
                    pausePlaying()
                } else {
                    startPlaying()
                }
            }
            KeyEvent.KEYCODE_MEDIA_PLAY -> {
                appAudioManager.getMediaController(this.requireContext()).dispatchMediaButtonEvent(event.keyEvent)
            }

            KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                appAudioManager.getMediaController(this.requireContext()).dispatchMediaButtonEvent(event.keyEvent)
            }
        }
    }

}