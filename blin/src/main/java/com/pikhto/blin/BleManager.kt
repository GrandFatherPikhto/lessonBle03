package com.pikhto.blin

import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class BleManager constructor(private val context: Context,
                             private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO)
    : DefaultLifecycleObserver {

    private val bluetoothManager:BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    val adapter get() = bluetoothAdapter
    private val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    val leScanner get() = bluetoothLeScanner

    val applicationContext:Context get() = context.applicationContext
    private val bleScanManager = BleScanManager(this, ioDispatcher)
    val scanner get() = bleScanManager

    val flowScanState get() = bleScanManager.flowState
    val scanState get()     = bleScanManager.flowState.value

    val flowScanDevice get() = bleScanManager.flowDevice
    val scannedDevices get() = bleScanManager.devices

    val flowScanError get() = bleScanManager.stateFlowError
    val scanError get()     = bleScanManager.valueError

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