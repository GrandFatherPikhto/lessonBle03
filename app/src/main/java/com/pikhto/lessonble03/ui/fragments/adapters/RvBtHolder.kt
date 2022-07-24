package com.pikhto.lessonble03.ui.fragments.adapters

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.recyclerview.widget.RecyclerView
import com.pikhto.lessonble03.R
import com.pikhto.lessonble03.databinding.LayoutBtDeviceBinding


class RvBtHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = LayoutBtDeviceBinding.bind(view)

    fun bind(scanResult: ScanResult) {
        binding.apply {
            scanResult.device.let { bluetoothDevice ->
                tvDeviceName.text =
                    bluetoothDevice.name
                        ?: itemView.context.getString(R.string.unknown_device)
                tvDeviceAddress.text = bluetoothDevice.address
                tvRssi.text = itemView.context.getString(R.string.rssi_title, scanResult.rssi)
                if (bluetoothDevice.bondState == BluetoothDevice.BOND_BONDED) {
                    ivBondState.setImageResource(R.drawable.ic_paired)
                } else {
                    ivBondState.setImageResource(R.drawable.ic_unpaired)
                }
                if (scanResult.isConnectable) {
                    ivConnectable.setImageResource(R.drawable.ic_connectable)
                } else {
                    ivConnectable.setImageResource(R.drawable.ic_no_connectable)
                }
            }
        }
    }
}