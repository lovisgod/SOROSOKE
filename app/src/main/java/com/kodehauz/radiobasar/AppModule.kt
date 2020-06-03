package com.kodehauz.radiobasar

import com.kodehauz.radiobasar.repo.AppRepo
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { AppRepo() }
//    single { Network }
//    single { getDatabase(androidContext()) }
//    single { BusinessRepo(get(), get()) }
}