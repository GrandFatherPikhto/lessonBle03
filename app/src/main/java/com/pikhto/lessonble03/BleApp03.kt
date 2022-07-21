package com.pikhto.lessonble03

import android.app.Application
import com.pikhto.blin.BleManager
import kotlin.properties.Delegates

class BleApp03 : Application() {
    var bleManager: BleManager? = null
}