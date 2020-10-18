package com.lovisgod.sorosoke

import com.lovisgod.sorosoke.repo.AppRepo
import org.koin.dsl.module

val appModule = module {
    single { AppRepo() }
}