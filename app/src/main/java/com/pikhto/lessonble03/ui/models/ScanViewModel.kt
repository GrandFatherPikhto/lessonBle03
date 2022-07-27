package com.pikhto.lessonble03.ui.models

import android.bluetooth.le.ScanResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pikhto.blin.BleManager
import com.pikhto.blin.BleScanManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ScanViewModel() : ViewModel() {
    private val mutableSharedFlowScanResult = MutableSharedFlow<ScanResult>()
    val sharedFlowScanResult get() = mutableSharedFlowScanResult.asSharedFlow()

    private val mutableStateFlowScanState = MutableStateFlow(BleScanManager.State.Stopped)
    val stateFLowScanState   get() = mutableStateFlowScanState.asSharedFlow()
    val scanState            get() = mutableStateFlowScanState.value

    private val mutableStateFlowScanError = MutableStateFlow(-1)
    val stateFlowError       get() = mutableStateFlowScanError.asSharedFlow()
    val scanError            get() = mutableStateFlowScanError.value
    var scanPressed          = false

    fun changeBleManager(bleManager: BleManager) {
        viewModelScope.launch {
            bleManager.sharedFlowScanReulst.collect {
                mutableSharedFlowScanResult.tryEmit(it)
            }
        }
        viewModelScope.launch {
            bleManager.stateFlowScanState.collect {
                mutableStateFlowScanState.tryEmit(it)
            }
        }
        viewModelScope.launch {
            bleManager.stateFlowScanError.collect {
                mutableStateFlowScanError.tryEmit(it)
            }
        }
    }
}