package com.pikhto.lessonble03.ui

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pikhto.blin.BleManager
import com.pikhto.blin.ScanIdling
import com.pikhto.lessonble03.BleApp03
import com.pikhto.lessonble03.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get: Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private val _bleManager:BleManager? by lazy {
        (ApplicationProvider.getApplicationContext() as BleApp03).bleManager
    }

    private val bleManager get() = _bleManager!!

    @Before
    fun setUp() {
        activityRule.scenario.onActivity {

        }
        activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        IdlingRegistry.getInstance().register(bleManager.scanner.getScanIdling())
    }

    @After
    fun tearDown() {
        activityRule.scenario.moveToState(Lifecycle.State.DESTROYED)
        IdlingRegistry.getInstance().unregister(bleManager.scanner.getScanIdling())
    }

    /**
     * Сканирует список устройств до первого обнаруженного устройства
     * После обнаружения устройства, отключает ожидание ScanIdle.scanned = true
     * Проверяет, что список устройств не пуст.
     * Кликает по плашке с нулевым устройством.
     * Проверяет переход на фрагмент устройств.
     * Проверяет содержимое фрагмента устройств.
     * Переходит обратно к фрагменту сканирования.
     */
    @Test(timeout = 10000)
    fun scanTwoDevicesAndClickDevice() {
        val unknownDevice = ApplicationProvider
            .getApplicationContext<BleApp03>()
            .getString(R.string.unknown_device)

        onView(withId(R.id.rv_bt_devices)).check(matches(isDisplayed()))
        onView(withId(R.id.action_scan)).perform(click())
        assertTrue(bleManager.scanner.devices.isNotEmpty())

        onView(withText(bleManager.scanner.devices[0].address)).perform(click())
        onView(withId(R.id.cl_ble_device)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_device_address))
            .check(matches(withText(bleManager.scanner.devices[0].address)))
        onView(withId(R.id.tv_device_name))
            .check(matches(withText(
                bleManager.scanner.devices[0].name ?: unknownDevice)))
    }
}