package com.pikhto.blin

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanRecord
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
// @RunWith(MockitoJUnitRunner::class)
@RunWith(RobolectricTestRunner::class)
class BleManagerTest {
    private lateinit var closeable:AutoCloseable

    @Before
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
    }

    @After
    fun tearDown() {
        closeable.close()
    }

    private val bleManager =
        BleManager(ApplicationProvider.getApplicationContext<Context?>().applicationContext,
            UnconfinedTestDispatcher())

    /**
     * Проверяет состояние после запуска сканирования.
     * "Запускает" набор сгенерированных BluetoothDevices
     * и проверяет список отфильтрованных устройств на совпадение
     * Останавливает сканирование и проверяет состояние flowState
     */
    @Test
    fun testScan() = runTest(UnconfinedTestDispatcher()) {
        val intent = mockRandomIntentScanResults(7, "Bluetooth%02d")
        bleManager.startScan()
        assertEquals(BleScanManager.State.Scanning, bleManager.scanState)
        bleManager.scanner.onReceiveScanResult(intent)
        assertEquals(bleManager.scanner.devices, intent.toBluetoothDevices())
        bleManager.stopScan()
        assertEquals(BleScanManager.State.Stopped, bleManager.scanState)
    }

    @Test
    fun testFilterScanNameWithStop() = runTest(UnconfinedTestDispatcher()) {
        val number = 2
        val intent = mockRandomIntentScanResults(7, "Bluetooth%02d")
        bleManager.startScan(names = listOf(intent.toBluetoothDevices()[number].name),
            stopOnFind = true)
        assertEquals(BleScanManager.State.Scanning, bleManager.scanState)
        bleManager.scanner.onReceiveScanResult(intent)
        assertEquals(bleManager.scanner.devices, listOf(intent.toBluetoothDevices()[number]))
        assertEquals(BleScanManager.State.Stopped, bleManager.scanState)
    }

    @Test
    fun testFilterScanAddressWithStop() = runTest(UnconfinedTestDispatcher()) {
        val number = 2
        val intent = mockRandomIntentScanResults(7, "Bluetooth%02d")
        bleManager.startScan(addresses = listOf(intent.toBluetoothDevices()[number].address),
            stopOnFind = true)
        assertEquals(BleScanManager.State.Scanning, bleManager.scanState)
        bleManager.scanner.onReceiveScanResult(intent)
        assertEquals(bleManager.scanner.devices, listOf(intent.toBluetoothDevices()[number]))
        assertEquals(BleScanManager.State.Stopped, bleManager.scanState)
    }
}