package com.lovisgod.sorosoke.models

import android.view.KeyEvent

data class AppEvent(
    var event: String
)

data class MediaEvent(
    var event: String,
    var code : Int,
    var keyEvent: KeyEvent
)