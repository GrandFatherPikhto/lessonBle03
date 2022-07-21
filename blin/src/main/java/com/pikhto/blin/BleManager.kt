package com.pikhto.blin

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class BleManager constructor(private val context: Context,
                             private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO)
    : DefaultLifecycleObserver {

    val applicationContext:Context get() = context.applicationContext
    private val bleScanManager = BleScanManager(this, ioDispatcher)

    val flowScanState get() = bleScanManager.flowState
    val scanState get()     = bleScanManager.flowState.value

    val flowScanDevice get() = bleScanManager.flowDevice
    val scannedDevices get() = bleScanManager.devices

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        owner.lifecycle.addObserver(bleScanManager)
    }

    fun startScan(addresses: List<String> = listOf(),
                  names: List<String> = listOf(),
                  services: List<String> = listOf(),
                  stopOnFind: Boolean = false,
                  filterRepeatable: Boolean = true,
                  stopTimeout: Long = 0L
    ) : Boolean = bleScanManager.startScan(addresses, names, services,
        stopOnFind, filterRepeatable, stopTimeout)

    fun stopScan() = bleScanManager.stopScan()
}