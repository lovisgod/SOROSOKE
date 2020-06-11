package com.kodehauz.radiobasar

import android.app.Application
import android.content.Context
import com.kodehauz.radiobasar.utils.PlayerManager
import com.pixplicity.easyprefs.library.Prefs
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MainApplication: Application() {
    var player = PlayerManager.getPlayer()
    override fun onCreate() {
        super.onCreate()
        // initailize pref library
        Prefs.Builder()
            .setContext(this)
            .setMode(Context.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()

        // start koin
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(applicationContext)
            modules(listOf(appModule))
        }
    }
}