package com.pikhto.lessonble03.ui.models

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pikhto.lessonble03.LessonBle03App
import java.lang.Exception

class BleViewModelProviderFactory constructor(private val application: Application): ViewModelProvider.Factory {
    private val logTag = this.javaClass.simpleName
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        (application.applicationContext as LessonBle03App).bleManager?.let {
            return ScanViewModel(it) as T
        }
        throw Exception("Не создан объект BleScanManager")
    }
}