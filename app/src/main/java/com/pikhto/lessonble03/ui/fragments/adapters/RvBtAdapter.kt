package com.pikhto.lessonble03.ui.fragments.adapters

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pikhto.lessonble03.R

typealias ItemOnClickListener<T> = (T, View) -> Unit
typealias ItemOnLongClickListener<T> = (T, View) -> Unit

class RvBtAdapter : RecyclerView.Adapter<RvBtHolder> () {
    private val scanResults = mutableListOf<ScanResult>()
    private var itemOnClickListener: ItemOnClickListener<ScanResult>? = null
    private var itemOnLongClickListener: ItemOnLongClickListener<ScanResult>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvBtHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_bt_device, parent, false)
        return RvBtHolder(view)
    }

    override fun onBindViewHolder(holder: RvBtHolder, position: Int) {
        holder.itemView.setOnClickListener { view ->
            itemOnClickListener?.let { listener ->
                listener(scanResults[position], view)
            }
        }
        holder.itemView.setOnLongClickListener { view ->
            itemOnLongClickListener?.let { listener ->
                listener(scanResults[position], view)
            }
            true
        }
        holder.bind(scanResults[position])
    }

    override fun getItemCount(): Int = scanResults.size

    fun addScanResult(scanResult: ScanResult) {
        if (!scanResults.map { it.device }.contains(scanResult.device)) {
            scanResults.add(scanResult)
            notifyItemInserted(scanResults.indexOf(scanResult))
        }
    }

    fun setItemOnClickListener(itemOnClickListener: ItemOnClickListener<ScanResult>) {
        this.itemOnClickListener = itemOnClickListener
    }

    fun setItemOnLongCliclListener(itemOnLongClickListener: ItemOnLongClickListener<ScanResult>) {
        this.itemOnLongClickListener = itemOnLongClickListener
    }
}