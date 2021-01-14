package com.enb.selde.utils

import android.graphics.Bitmap

class RxEvent {

    data class EventIsLandscape(val mode: Boolean)

    data class EventBackgroundImg(val img: Bitmap)
}