package com.lovisgod.sorosoke.ui.activity

import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.appbar.AppBarLayout
import com.lovisgod.sorosoke.R
import com.lovisgod.sorosoke.models.MediaEvent
import org.greenrobot.eventbus.EventBus


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var toolbar: Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        appBarLayout = findViewById(R.id.tool_bar_layout)
//        toolbar = findViewById(R.id.tool_bar)
//        toolbar.inflateMenu(R.menu.app_menu)
        navController = Navigation.findNavController(this, R.id.app_nav_host_fragment)
//        val appBarConfiguration = AppBarConfiguration.Builder(navController.graph).build()
//        NavigationUI.setupWithNavController(toolbar, navController)
//        toolbar.setOnMenuItemClickListener {
//            return@setOnMenuItemClickListener when(it.itemId){
//                R.id.contactFragment -> {
//                    navController.navigate(R.id.contactFragment)
//                    true
//                }
//                R.id.aboutFragment -> {
//                    navController.navigate(R.id.aboutFragment)
//                    true
//                }
//                else -> false
//            }
//        }

        volumeControlStream = AudioManager.STREAM_MUSIC


    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK) {
                EventBus.getDefault().post(MediaEvent(event = "mediaEvent", code = keyCode, keyEvent = event))
            }
            return super.onKeyDown(keyCode, event)
        }
        when (keyCode) {

            KeyEvent.KEYCODE_HEADSETHOOK -> {
                EventBus.getDefault().post(MediaEvent(event = "mediaEvent", code = keyCode, keyEvent = event))
            }
            KeyEvent.KEYCODE_MEDIA_PLAY -> {
                return true
            }

            KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                return  true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
    
}
