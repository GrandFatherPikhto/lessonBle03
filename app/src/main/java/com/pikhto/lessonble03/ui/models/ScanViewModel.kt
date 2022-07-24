package com.pikhto.lessonble03.ui.models

import android.bluetooth.le.ScanResult
import androidx.lifecycle.ViewModel
import com.pikhto.blin.BleScanManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class ScanViewModel : ViewModel() {
    private val scanResults = mutableListOf<ScanResult>()

    private val mutableSharedFlowScanResult = MutableSharedFlow<ScanResult>(replay = 100)
    val sharedFlowScanResult get() = mutableSharedFlowScanResult.asSharedFlow()

    private val mutableStateFlowScanState = MutableStateFlow(BleScanManager.State.Stopped)
    val stateFlowScanState get() = mutableStateFlowScanState.asStateFlow()
    val scanState get() = mutableStateFlowScanState.value

    private val mutableStateFlowScanError = MutableStateFlow(-1)
    val stateFlowScanError get() = mutableStateFlowScanError.asStateFlow()
    val scanError get() = mutableStateFlowScanError.value

    fun addScanResult(scanResult: ScanResult) {
        if (!scanResults.map { it.device }.contains(scanResult.device)) {
            mutableSharedFlowScanResult.tryEmit(scanResult)
        }
    }

    fun changeScanState(scanState: BleScanManager.State) {
        mutableStateFlowScanState.tryEmit(scanState)
    }

    fun changeScanError(errorCode: Int) {
        mutableStateFlowScanError.tryEmit(errorCode)
    }
}