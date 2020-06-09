package com.kodehauz.radiobasar.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.kodehauz.radiobasar.repo.AppRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject

class AppViewModel(application: Application): ViewModel() {

   val appRepo by application.inject<AppRepo> ()

    init {

    }

    fun submitUserData(name: String, email:String) = appRepo.getUserDetails(name, email)



    /**
     * Factory for constructing AppViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AppViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}