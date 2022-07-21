package com.pikhto.lessonble03.ui.fragments.adapters

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pikhto.lessonble03.R

typealias ItemOnClickListener<T> = (T, View) -> Unit
typealias ItemOnLongClickListener<T> = (T, View) -> Unit

class RvBtAdapter : RecyclerView.Adapter<RvBtHolder> () {
    private val devices = mutableListOf<BluetoothDevice>()
    private var itemOnClickListener: ItemOnClickListener<BluetoothDevice>? = null
    private var itemOnLongClickListener: ItemOnLongClickListener<BluetoothDevice>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvBtHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_bt_device, parent, false)
        return RvBtHolder(view)
    }

    override fun onBindViewHolder(holder: RvBtHolder, position: Int) {
        holder.itemView.setOnClickListener { view ->
            itemOnClickListener?.let { listener ->
                listener(devices[position], view)
            }
        }
        holder.itemView.setOnLongClickListener { view ->
            itemOnLongClickListener?.let { listener ->
                listener(devices[position], view)
            }
            true
        }
        holder.bind(devices[position])
    }

    override fun getItemCount(): Int = devices.size

    fun addDevice(bluetoothDevice: BluetoothDevice) {
        if (!devices.contains(bluetoothDevice)) {
            devices.add(bluetoothDevice)
            notifyItemInserted(devices.indexOf(bluetoothDevice))
        }
    }

    fun setItemOnClickListener(itemOnClickListener: ItemOnClickListener<BluetoothDevice>) {
        this.itemOnClickListener = itemOnClickListener
    }

    fun setItemOnLongCliclListener(itemOnLongClickListener: ItemOnLongClickListener<BluetoothDevice>) {
        this.itemOnLongClickListener = itemOnLongClickListener
    }
}