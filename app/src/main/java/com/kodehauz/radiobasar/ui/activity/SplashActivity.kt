package com.kodehauz.radiobasar.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.kodehauz.radiobasar.R

class SplashActivity : AppCompatActivity() {
    private lateinit var topAnim: Animation
    private lateinit var image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash)

        topAnim = AnimationUtils.loadAnimation(this,
            R.anim.top_animation
        )

        image = findViewById(R.id.imageView)

        image.animation = topAnim

        //4 seconds splash time
        Handler().postDelayed({
            //Start mainActivity
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        },4000)


    }
}
