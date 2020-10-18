package com.lovisgod.sorosoke.utils

import android.media.MediaPlayer

class PlayerManager: MediaPlayer() {

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: MediaPlayer? = null

        fun getPlayer(): MediaPlayer? {
            val tempInstance = INSTANCE
            // if the instance is not null use the already created instance
            // else if the instance is null, build an instance of MediaPlayer
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                var instance = MediaPlayer()
                INSTANCE = instance
                return instance
            }
        }
    }
}