package com.pikhto.blin

import androidx.test.espresso.IdlingResource
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.properties.Delegates

class ScanIdling : IdlingResource {
    companion object {
        private var scanIdling:ScanIdling? = null
        fun getInstance() : ScanIdling {
            return scanIdling ?: ScanIdling()
        }
    }

    private var resourceCallback: IdlingResource.ResourceCallback? = null

    private var isNavigated = AtomicBoolean(true)

    var scanned by Delegates.observable(true) { _, _, newState ->
        isNavigated.set(newState)
        resourceCallback?.let { callback ->
            if (newState) {
                callback.onTransitionToIdle()
            }
        }
    }

    override fun getName(): String = this.javaClass.simpleName

    override fun isIdleNow(): Boolean = isNavigated.get()

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        resourceCallback = callback
    }
}