package com.lovisgod.sorosoke.ui.fragment

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
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.lovisgod.sorosoke.MainApplication
import com.lovisgod.sorosoke.R
import com.lovisgod.sorosoke.databinding.FragmentPlayerBinding
import com.lovisgod.sorosoke.models.AppEvent
import com.lovisgod.sorosoke.models.ErrorEvent
import com.lovisgod.sorosoke.models.MediaEvent
import com.lovisgod.sorosoke.ui.bottomSheet.CommentBottomSheet
import com.lovisgod.sorosoke.utils.*
import com.lovisgod.sorosoke.viewmodel.AppViewModel
import com.pixplicity.easyprefs.library.Prefs
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import kotlin.Exception


/**
 * A simple [Fragment] subclass.
 * Use the [PlayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlayerFragment : Fragment(),  Playable {
    private lateinit var binding: FragmentPlayerBinding
    private lateinit var navController: NavController
    private var player = MainApplication().player!!
    private lateinit var appAudioManager: AppAudioManger
    private lateinit var audioManager: AudioManager
    private var playing: Boolean = false
    private var muted: Boolean = false
    private lateinit var playButton: ImageView
    val ACTION_PLAY = "PLAY"
    val ACTION_PAUSE = "PAUSE"
    var initialProgressValue = 0
    private var stopped: Boolean = false
    private var pausedAudioLoss = false
    private lateinit var dataDialog: AlertDialog


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
            try {
                if (playing) {
                    pausePlaying()
                    playing = false
                    println(playing)
                } else {
                    startPlaying()
                    playing = true
                    println(playing)
                }
            } catch (e: Exception) {
                println(e.localizedMessage)
            }
        }

        binding.shareBtn.setOnClickListener {
            val myIntent = Intent(Intent.ACTION_SEND)
            myIntent.type = "text/plain"
            myIntent.putExtra(Intent.EXTRA_SUBJECT, "SOROSOKE")
            val shareMessage = "With one  voice we see to the rebirth of our nation"
            myIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(myIntent,"Share via"))
        }

        binding.handImage.setOnClickListener {
            navController.navigate(R.id.aboutFragment)
        }

        binding.sound.setOnClickListener {
            try {
                if ( muted ) {
                    appAudioManager.unMuteVolume(audioManager)
                    muted = false
                    binding.sound.setColorFilter(this.requireContext().resources.getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP)
                }

                else {
                    appAudioManager.muteVolume(audioManager)
                    muted = true
                    binding.sound.setColorFilter(this.requireContext().resources.getColor(R.color.redcolor), PorterDuff.Mode.SRC_ATOP);
                }
            } catch (e: Exception) {
                println(e.localizedMessage)
            }

        }

        binding.commentBtn.setOnClickListener{
            val bottomSheet = CommentBottomSheet.newInstance(R.layout.coming_soon, viewModel)
            bottomSheet?.show(this.requireActivity().supportFragmentManager.beginTransaction(), "dialog_comment")
//            val firstTimeComment = Prefs.getBoolean("first_time_comment", false)
//            if (!firstTimeComment){
//                dataDialog = Dialog().displayInputContactDialog(this.requireContext(), viewModel)!!
//                dataDialog.show()
//            } else {
//                val bottomSheet = CommentBottomSheet.newInstance(R.layout.comment_layout, viewModel)
//                bottomSheet?.show(this.requireActivity().supportFragmentManager.beginTransaction(), "dialog_comment")
//            }

        }

        viewModel._installCount.observe(viewLifecycleOwner, Observer {
            binding.numbers.text = it.toString()
        })

        if (player.isPlaying) {
            playButton.setImageDrawable(this.requireContext().resources.getDrawable(R.drawable.ic_stop))
            showNotification(R.drawable.ic_pause_black_24dp)
            playing = true
        }

       return binding.root
    }

    private fun startPlaying() {
        playButton.setImageDrawable(this.requireContext().resources.getDrawable(R.drawable.ic_stop))


        if (!stopped) {
            makeFocus()
        } else if (!stopped && !playing) {
            player.start()
            playing = true
        } else {
            initializeMediaPlayer()
        }

        playing = true
        showNotification(R.drawable.ic_pause_black_24dp)
    }

    private fun pausePlaying() {
            playButton.setImageDrawable(this.requireContext().resources.getDrawable(R.drawable.ic_play))
            player.pause()
            playing = false
            showNotification(R.drawable.ic_play_arrow_black_24dp)

    }

    private fun initializeMediaPlayer() {
        if (!player.isPlaying ) {
            val dailog = LoadingDialog[this.requireActivity()]

            if (!stopped) {
                dailog.show()
            }

//            val url = Environment.RADIO_URL
//        val url = "https://s25.myradiostream.com/15102/listen.mp3"
            val url = "https://s4.radio.co/s99d55c85b/listen"
            try {
                player.setDataSource(url)
                player.prepareAsync()
                player.setOnBufferingUpdateListener { _, percent ->  println(percent) }
                player.setOnPreparedListener {
                    if (!stopped) {
                        dailog.hide()
                    } else {
                        it.start()
                        playing = true
                        stopped = false
                    }

                }

                player.setOnCompletionListener { p0 ->
                    p0?.stop()
                    p0.reset()
                    playButton.setImageDrawable(requireContext().resources.getDrawable(R.drawable.ic_play))
                    playing = false
                    stopped = true
                    showNotification(R.drawable.ic_play_arrow_black_24dp)
                }


            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                dailog.hide()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
                dailog.hide()
            } catch (e: IOException) {
                e.printStackTrace()
                dailog.hide()
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
        // unregister the event listener
        EventBus.getDefault().unregister(this)
    }

    override fun onStart() {
        super.onStart()
//        player.seekTo(0)
        println(player.isPlaying)
        if (player.isPlaying) {

        }
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
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.RED
                notificationChannel.enableVibration(false)
                notificationChannel.description = "Arise New Nigeria"

                val notificationManager = this.requireContext().getSystemService(
                    NotificationManager::class.java
                )
                notificationManager?.createNotificationChannel(notificationChannel)
            }
        } catch (e: Exception) {
            println(e.localizedMessage)
        }



    }


    private fun showNotification(playBtn:Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = this.requireContext().getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.sendNotification(
                "Arise New Nigeria",
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
                playButton.setImageDrawable(this.requireContext().resources.getDrawable(R.drawable.ic_play))
                pausedAudioLoss = true
                showNotification(R.drawable.ic_play_arrow_black_24dp)
            }
            "dataCaptureSuccess" -> {
                Dialog().makeSnack(binding.commentBtn, "Thanks for registering your voice", this.requireContext())
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
            "dataCaptureError", "commentSaveError" -> {
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

    fun makeFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appAudioManager.requestFocus(audioManager)
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            appAudioManager.requestFocusLowerVersion(audioManager)
        }
    }

}