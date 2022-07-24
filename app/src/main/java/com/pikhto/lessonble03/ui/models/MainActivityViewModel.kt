package com.pikhto.lessonble03.ui.models

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivityViewModel : ViewModel() {
    private val mutableStateFlowScanResult = MutableStateFlow<ScanResult?>(null)
    val stateFlowScanResult get() = mutableStateFlowScanResult.asStateFlow()
    val scanResult get() = mutableStateFlowScanResult.value
    val device get() = mutableStateFlowScanResult.value?.device

    fun changeScanResult(scanResult: ScanResult) {
        mutableStateFlowScanResult.tryEmit(scanResult)
    }
}