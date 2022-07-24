package com.pikhto.lessonble03.ui.models

import android.bluetooth.le.ScanResult
import androidx.lifecycle.ViewModel
import com.pikhto.blin.BleManager
import com.pikhto.blin.BleScanManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class ScanViewModel(bleManager: BleManager) : ViewModel() {
    val sharedFlowScanResult = bleManager
    val stateFLowScanState   = bleManager.stateFlowScanState
    val scanState            = bleManager.scanState
    val stateFlowError       = bleManager.stateFlowScanError
    val scanError            = bleManager.scanError
}