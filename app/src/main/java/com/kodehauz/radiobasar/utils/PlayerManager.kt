package com.kodehauz.radiobasar.utils

import android.media.MediaPlayer

class PlayerManager: MediaPlayer() {

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: PlayerManager? = null

        fun getPlayer(): PlayerManager? {
            val tempInstance = INSTANCE
            // if the instance is not null use the already created instance
            // else if the instance is null, build an instance of MediaPlayer
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                var instance = PlayerManager()
                INSTANCE = instance
                return instance
            }
        }
    }
}