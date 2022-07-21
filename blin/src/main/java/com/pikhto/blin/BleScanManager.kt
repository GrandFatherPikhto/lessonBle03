package com.pikhto.blin

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.ParcelUuid
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class BleScanManager constructor(private val bleManager: BleManager,
                                 ioDispatcher: CoroutineDispatcher = Dispatchers.IO)
    : DefaultLifecycleObserver {

    private val bcScanReceiver:BcScanReceiver = BcScanReceiver(this)

    val applicationContext:Context get() = bleManager.applicationContext

    enum class State (val value: Int) {
        Stopped(0x00),
        Scanning(0x01),
        Error(0x03)
    }

    private val logTag = this.javaClass.simpleName



    private val mutableStateFlowScanning = MutableStateFlow(State.Stopped)
    val flowState get() = mutableStateFlowScanning.asStateFlow()
    private val state get() = mutableStateFlowScanning.value

    private val mutableSharedFlowDevice = MutableSharedFlow<BluetoothDevice>(replay = 100)
    val flowDevice get() = mutableSharedFlowDevice.asSharedFlow()

    private val mutableStateFlowError = MutableStateFlow<Int>(-1)
    val stateFlowError get() = mutableStateFlowError.asStateFlow()
    val valueError get() = mutableStateFlowError.value
    private var bleScanPendingIntent:PendingIntent = bcScanReceiver.pendingIntent


    private val bluetoothLeScanner by lazy {
        (applicationContext.getSystemService(Context.BLUETOOTH_SERVICE)
                as BluetoothManager).adapter.bluetoothLeScanner
    }

    private val scanFilters = mutableListOf<ScanFilter>()
    private val scanSettingsBuilder = ScanSettings.Builder()

    private var scope = CoroutineScope(ioDispatcher)
    private var notEmitRepeat: Boolean = true
    private val scannedDevices = mutableListOf<BluetoothDevice>()
    val devices get() = scannedDevices.toList()

    private var stopOnFind = false
    private var stopTimeout = 0L

    private val addresses = mutableListOf<String>()
    private val names = mutableListOf<String>()
    private val uuids = mutableListOf<ParcelUuid>()

    init {
        initScanSettings()
        initScanFilters()
    }

    @SuppressLint("MissingPermission")
    fun startScan(addresses: List<String> = listOf(),
                  names: List<String> = listOf(),
                  services: List<String> = listOf(),
                  stopOnFind: Boolean = false,
                  filterRepeatable: Boolean = true,
                  stopTimeout: Long = 0L
    ) : Boolean {
        if (state == State.Error) {
            mutableStateFlowScanning.tryEmit(State.Stopped)
        }

        if (state == State.Stopped) {
            // devices.clear()

            Log.d(logTag, "startScan()")

            this.addresses.clear()
            this.addresses.addAll(addresses)

            this.names.clear()
            this.names.addAll(names)

            this.stopOnFind = stopOnFind
            this.notEmitRepeat = filterRepeatable

            this.uuids.clear()
            this.uuids.addAll(services.mapNotNull { ParcelUuid.fromString(it) }
                .toMutableList())

            if (stopTimeout > 0) {
                scope.launch {
                    this@BleScanManager.stopTimeout = stopTimeout
                    delay(stopTimeout)
                    stopScan()
                }
            }

            val result = bluetoothLeScanner.startScan(
                scanFilters,
                scanSettingsBuilder.build(),
                bleScanPendingIntent
            )
            if (result == 0) {
                mutableStateFlowScanning.tryEmit(State.Scanning)
                return true
            } else {
                mutableStateFlowScanning.tryEmit(State.Error)
            }
        }

        return false
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        if (state == State.Scanning) {
            Log.d(logTag, "stopScan()")
            bluetoothLeScanner.stopScan(bleScanPendingIntent)
            mutableStateFlowScanning.tryEmit(State.Stopped)
        }
    }


    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        Log.d(logTag, "onCreate()")
        bleManager.applicationContext.registerReceiver(bcScanReceiver, makeIntentFilters())
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        bleManager.applicationContext.unregisterReceiver(bcScanReceiver)
    }

    private fun initScanFilters() {
        val filter = ScanFilter.Builder().build()
        scanFilters.add(filter)
    }

    @SuppressLint("MissingPermission")
    private fun filterName(bluetoothDevice: BluetoothDevice) : Boolean =
        names.isEmpty()
            .or(names.isNotEmpty()
                .and(bluetoothDevice.name != null)
                .and(names.contains(bluetoothDevice.name)))

    private fun filterAddress(bluetoothDevice: BluetoothDevice) : Boolean =
        addresses.isEmpty()
            .or(addresses.isNotEmpty().and(addresses.contains(bluetoothDevice.address)))

    private fun filterUuids(uuids: Array<ParcelUuid>?) : Boolean {
        if (this.uuids.isEmpty()) return true
        // println("UUIDS: ${this.uuids}")
        if (uuids.isNullOrEmpty()) return false
        if (this.uuids.containsAll(uuids.toList())) return true
        return false
    }

    @SuppressLint("MissingPermission")
    private fun filterDevice (bluetoothDevice: BluetoothDevice) {
        if (filterName(bluetoothDevice)
                .and(filterAddress(bluetoothDevice))
                .and(filterUuids(bluetoothDevice.uuids))
        )
            if (notEmitRepeat) {
                if (!scannedDevices.contains(bluetoothDevice)) {
                    mutableSharedFlowDevice.tryEmit(bluetoothDevice)
                    scannedDevices.add(bluetoothDevice)
                }
            } else {
                mutableSharedFlowDevice.tryEmit(bluetoothDevice)
            }
    }

    /**
     * Проверяем ошибки.
     */
    private fun isLeScanNoError(intent: Intent) : Boolean {
        if (intent.hasExtra(BluetoothLeScanner.EXTRA_CALLBACK_TYPE)) {
            val callbackType =
                intent.getIntExtra(BluetoothLeScanner.EXTRA_CALLBACK_TYPE, -1)
            if (callbackType >= 0) {
                if (intent.hasExtra(BluetoothLeScanner.EXTRA_ERROR_CODE)) {
                    val errorCode
                            = intent.getIntExtra(BluetoothLeScanner.EXTRA_ERROR_CODE, -1 )
                    mutableStateFlowError.tryEmit(errorCode)
                    Log.e(logTag, "Scan error: $errorCode")
                    return false
                }
            }
        }
        return true
    }

    fun onReceiveScanResult(intent: Intent) {
        Log.d(logTag, "onReceiveScanResult()")
        if (isLeScanNoError(intent)) {
            if (intent.hasExtra(BluetoothLeScanner.EXTRA_LIST_SCAN_RESULT)) {
                intent.getParcelableArrayListExtra<ScanResult>(BluetoothLeScanner.EXTRA_LIST_SCAN_RESULT)
                    ?.let { results ->
                        results.forEach { result ->
                            result.device?.let { device ->
                                Log.d(logTag, "Device: $device")
                                filterDevice(device)
                            }
                        }
                    }
            }
        }
    }

    private fun makeIntentFilters() : IntentFilter = IntentFilter().let { intentFilter ->
        intentFilter.addAction(Intent.CATEGORY_DEFAULT)
        intentFilter.addAction(BcScanReceiver.ACTION_BLE_SCAN)
        intentFilter
    }

    private fun initScanSettings() {
        scanSettingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        scanSettingsBuilder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
        // setReportDelay() -- отсутствует. Не вызывать! Ответ приходит ПУСТОЙ!
        // В официальной документации scanSettingsBuilder.setReportDelay(1000)
        scanSettingsBuilder.setNumOfMatches(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT)
        scanSettingsBuilder.setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
        scanSettingsBuilder.setLegacy(false)
        scanSettingsBuilder.setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED)
    }
}