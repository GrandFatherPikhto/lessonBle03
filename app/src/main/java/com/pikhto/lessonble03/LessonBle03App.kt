package com.pikhto.lessonble03

import android.app.Application
import com.pikhto.blin.BleManager
import kotlin.properties.Delegates

class LessonBle03App : Application() {
    var bleManager: BleManager? = null
}