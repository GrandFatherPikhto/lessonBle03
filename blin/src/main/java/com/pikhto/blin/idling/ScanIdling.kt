package com.pikhto.blin.idling

import androidx.test.espresso.IdlingResource
import com.pikhto.blin.BleManager
import com.pikhto.blin.BleScanManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

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

    init {
        scope.launch {
            bleManager.stateFlowScanState.collect { state ->
                when(state) {
                    BleScanManager.State.Stopped -> {
                        isIdling.set(true)
                        resourceCallback?.let { callback ->
                            callback.onTransitionToIdle()
                        }
                    }
                    BleScanManager.State.Scanning -> {
                        isIdling.set(false)
                    }
                    else -> { }
                }
            }
        }
        scope.launch {
            bleManager.sharedFlowScanReulst.collect {
                if (it.isConnectable) {
                    isIdling.set(true)
                    resourceCallback?.let { callback ->
                        callback.onTransitionToIdle()
                    }
                }
            }
        }
    }

    override fun getName(): String = this.javaClass.simpleName

    override fun isIdleNow(): Boolean = isIdling.get()

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        resourceCallback = callback
    }
}