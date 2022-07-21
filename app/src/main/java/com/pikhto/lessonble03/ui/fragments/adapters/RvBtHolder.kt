package com.pikhto.lessonble03.ui.fragments.adapters

import android.bluetooth.BluetoothDevice
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.pikhto.lessonble03.R
import com.pikhto.lessonble03.databinding.LayoutBtDeviceBinding

class RvBtHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = LayoutBtDeviceBinding.bind(view)

    fun bind(bluetoothDevice: BluetoothDevice) {
        binding.apply {
            tvDeviceName.text =
                bluetoothDevice.name
                    ?: itemView.context.getString(R.string.unknown_device)
            tvDeviceAddress.text = bluetoothDevice.address
            if (bluetoothDevice.bondState == BluetoothDevice.BOND_BONDED) {
                ivBondState.setImageResource(R.drawable.ic_paired)
            } else {
                ivBondState.setImageResource(R.drawable.ic_unpaired)
            }
        }
    }
}