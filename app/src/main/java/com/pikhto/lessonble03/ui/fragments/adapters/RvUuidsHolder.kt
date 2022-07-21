package com.pikhto.lessonble03.ui.fragments.adapters

import android.bluetooth.BluetoothDevice
import android.os.ParcelUuid
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.pikhto.lessonble03.databinding.LayoutUuidBinding
import java.util.*

class RvUuidsHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = LayoutUuidBinding.bind(view)

    fun bind(uuid: ParcelUuid) {
        binding.apply {
            tvUuid.text = uuid.toString()
        }
    }
}