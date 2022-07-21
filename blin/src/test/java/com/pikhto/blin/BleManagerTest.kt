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

    @Test
    fun testBluetoothDevice() = runTest(UnconfinedTestDispatcher()) {
        val intent = mockRandomIntentScanResults(7, "Bluetooth%02d")
        intent.toBluetoothDevices().forEach {
            println("BluetoothDevice: ${it.name}")
        }
    }
}