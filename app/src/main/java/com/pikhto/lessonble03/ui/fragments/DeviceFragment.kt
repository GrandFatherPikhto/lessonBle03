package com.pikhto.lessonble03.ui.fragments

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuHost
import androidx.fragment.app.Fragment
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pikhto.lessonble03.LessonBle03App
import com.pikhto.lessonble03.R
import com.pikhto.lessonble03.databinding.FragmentDeviceBinding
import com.pikhto.lessonble03.helper.linkMenu
import com.pikhto.lessonble03.ui.fragments.adapters.RvUuidsAdapter
import com.pikhto.lessonble03.ui.models.MainActivityViewModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class DeviceFragment : Fragment() {
    private val logTag = this.javaClass.simpleName

    private var _binding: FragmentDeviceBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val mainActivityViewModel by activityViewModels<MainActivityViewModel>()
    private val bleManager by lazy {
        (requireContext().applicationContext as LessonBle03App).bleManager }
    private val rvUuidsAdapter = RvUuidsAdapter()

    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.menu_device, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.action_to_scanner -> {
                    Log.d(logTag, "Action To Scanner")
                    findNavController().navigate(R.id.action_deviceFragment_to_scanFragment)
                    true
                }
                else -> {
                    true
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDeviceBinding.inflate(inflater, container, false)
        binding.apply {
            mainActivityViewModel.scanResult?.let { scanResult ->
                includeLayout.tvDeviceName.text =
                    scanResult.device.name ?: requireContext().getString(R.string.unknown_device)
                includeLayout.tvDeviceAddress.text =
                    scanResult.device.address
                includeLayout.tvRssi.text = getString(R.string.rssi_title, scanResult.rssi)
                if (scanResult.device.bondState == BluetoothDevice.BOND_BONDED) {
                    includeLayout.ivBondState.setImageResource(R.drawable.ic_paired)
                } else {
                    includeLayout.ivBondState.setImageResource(R.drawable.ic_unpaired)
                }
                if (scanResult.isConnectable) {
                    includeLayout.ivConnectable.setImageResource(R.drawable.ic_connectable)
                } else {
                    includeLayout.ivConnectable.setImageResource(R.drawable.ic_no_connectable)
                }
                Log.d(logTag, "UUIDs: ${scanResult.device.uuids}")
            }

            rvBtUuids.adapter = rvUuidsAdapter
            rvBtUuids.layoutManager = LinearLayoutManager(requireContext())
        }

        mainActivityViewModel.device?.let { bluetoothDevice ->
            rvUuidsAdapter.bluetoothDevice = bluetoothDevice
        }

        linkMenu(true, menuProvider)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        linkMenu(false, menuProvider)
        _binding = null
    }
}