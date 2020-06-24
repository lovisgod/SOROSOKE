package com.kodehauz.radiobasar

import com.kodehauz.radiobasar.repo.AppRepo
import com.kodehauz.radiobasar.utils.HtmlLoader
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { AppRepo() }
    single { HtmlLoader() }
}