package com.pikhto.blin

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log

class BcScanReceiver constructor(private val bleScanManager: BleScanManager) : BroadcastReceiver() {
    companion object {
        const val ACTION_BLE_SCAN = "com.pikhto.lessonble03.ACTION_BLE_SCAN"
        const val REQUEST_CODE_BLE_SCANNER_PENDING_INTENT = 1000
    }
    private val logTag = this.javaClass.simpleName
    private val bcPendingIntent: PendingIntent by lazy {
        PendingIntent.getBroadcast(
            bleScanManager.applicationContext,
            REQUEST_CODE_BLE_SCANNER_PENDING_INTENT,
            Intent(ACTION_BLE_SCAN),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    val pendingIntent get() = bcPendingIntent

    override fun onReceive(context: Context?, intent: Intent?) {
        if ( context != null && intent != null ) {
            when (intent.action) {
                ACTION_BLE_SCAN -> {
                    bleScanManager.onReceiveScanResult(intent)
                }
                else -> {
                    Log.d(logTag, "Action: ${intent.action}")
                }
            }
        }
    }
}