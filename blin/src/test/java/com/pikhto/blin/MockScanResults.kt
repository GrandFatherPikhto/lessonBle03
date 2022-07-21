package com.pikhto.blin

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanRecord
import android.bluetooth.le.ScanResult
import android.content.Intent
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.robolectric.RuntimeEnvironment
import kotlin.random.Random


fun mockBluetoothDevice(name: String? = null, address: String? = null): BluetoothDevice {
    val bluetoothDevice = mock<BluetoothDevice>()
    Mockito.lenient().`when`(bluetoothDevice.name).thenReturn(name)
    Mockito.lenient().`when`(bluetoothDevice.address)
        .thenReturn(address ?: Random.nextBytes(6)
            .joinToString (":") { String.format("%02X", it) })
    return bluetoothDevice
}

fun mockScanResult(bluetoothDevice: BluetoothDevice) : ScanResult = ScanResult(
    bluetoothDevice,
    0, 0, 0, 0, 0, 0, 0,
    mock<ScanRecord>(),
    System.currentTimeMillis())

fun mockScanResultIntent(scanResults: List<ScanResult>) : Intent
        = Intent(RuntimeEnvironment.getApplication().applicationContext, ScanResult::class.java)
    .putParcelableArrayListExtra(
            BluetoothLeScanner.EXTRA_LIST_SCAN_RESULT,
            scanResults.toCollection(ArrayList()))

fun mockRandomIntentScanResults(num: Int, name: String? = null) : Intent =
    Intent(RuntimeEnvironment.getApplication().applicationContext, ScanResult::class.java)
        .putParcelableArrayListExtra(BluetoothLeScanner.EXTRA_LIST_SCAN_RESULT,
            (1..num).map { number ->
                mockScanResult(
                    mockBluetoothDevice(name = String.format(name ?: "BLE_%02d", number)))
            }.toCollection(ArrayList()))

fun Intent.toBluetoothDevices() : List<BluetoothDevice>
    = this.getParcelableArrayListExtra<ScanResult>(BluetoothLeScanner.EXTRA_LIST_SCAN_RESULT)
        ?.let { arrayList: java.util.ArrayList<ScanResult> ->
            arrayList.map { scanResult -> scanResult.device }
        } ?: listOf()
