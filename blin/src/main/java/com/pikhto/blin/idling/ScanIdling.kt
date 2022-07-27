package com.pikhto.blin.idling

import androidx.test.espresso.IdlingResource
import com.pikhto.blin.BleManager
import com.pikhto.blin.BleScanManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.properties.Delegates

class ScanIdling (private val bleManager: BleManager): IdlingResource {

    companion object {
        private var scanIdling: ScanIdling? = null
        fun getInstance(bleManager: BleManager) : ScanIdling {
            return scanIdling ?: ScanIdling(bleManager)
        }
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    private var resourceCallback: IdlingResource.ResourceCallback? = null

    private var isIdling = AtomicBoolean(true)

    var idling by Delegates.observable(false) { _, _, newState ->
        isIdling.set(newState)
        if (newState) {
            resourceCallback?.onTransitionToIdle()
        }
    }


    init {
        scope.launch {
            bleManager.stateFlowScanState.collect { state ->
                when(state) {
                    BleScanManager.State.Stopped -> {
                        idling = true
                    }
                    BleScanManager.State.Scanning -> {
                        idling = false
                    }
                    else -> { }
                }
            }
        }
        scope.launch {
            bleManager.sharedFlowScanReulst.collect {
                idling = it.isConnectable
            }
        }
    }

    override fun getName(): String = this.javaClass.simpleName

    override fun isIdleNow(): Boolean = isIdling.get()

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        resourceCallback = callback
    }
}