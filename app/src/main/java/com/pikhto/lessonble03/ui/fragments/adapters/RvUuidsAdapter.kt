package com.pikhto.lessonble03.ui.fragments.adapters

import android.bluetooth.BluetoothDevice
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pikhto.lessonble03.R
import kotlin.properties.Delegates

class RvUuidsAdapter : RecyclerView.Adapter<RvUuidsHolder>() {
    private val logTag = this.javaClass.simpleName

    var bluetoothDevice:BluetoothDevice? by Delegates.observable(null) { _, oldValue, newValue ->
        Log.d(logTag, "bluetoothDevice: $newValue ${newValue?.uuids?.size}")
        if (newValue == null) {
            notifyItemRangeRemoved(0, oldValue?.uuids?.size ?: 0)
        } else {
            notifyItemRangeInserted(0, newValue.uuids?.size ?: 0)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvUuidsHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_uuid, parent, false)

        return RvUuidsHolder(view)
    }

    override fun onBindViewHolder(holder: RvUuidsHolder, position: Int) {
        bluetoothDevice?.let { device ->
            holder.bind(device.uuids[position])
        }
    }

    override fun getItemCount(): Int = bluetoothDevice?.uuids?.size ?: 0
}