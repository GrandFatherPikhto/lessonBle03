package com.pikhto.lessonble03.ui.models

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivityViewModel : ViewModel() {
    private val mutableStateFlowDevice = MutableStateFlow<BluetoothDevice?>(null)
    val stateFlowDevice get() = mutableStateFlowDevice.asStateFlow()
    val device get() = mutableStateFlowDevice.value

    fun emitDevice(bluetoothDevice: BluetoothDevice) {
        mutableStateFlowDevice.tryEmit(bluetoothDevice)
    }
}