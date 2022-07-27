package com.pikhto.lessonble03.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pikhto.blin.BleManager
import com.pikhto.blin.BleScanManager
import com.pikhto.lessonble03.LessonBle03App
import com.pikhto.lessonble03.R
import com.pikhto.lessonble03.databinding.FragmentScanBinding
import com.pikhto.lessonble03.helper.linkMenu
import com.pikhto.lessonble03.ui.fragments.adapters.RvBtAdapter
import com.pikhto.lessonble03.ui.models.MainActivityViewModel
import com.pikhto.lessonble03.ui.models.ScanViewModel
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ScanFragment : Fragment() {
    private val logTag = this.javaClass.simpleName

    private var _binding: FragmentScanBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val rvBtAdapter = RvBtAdapter()

    private val mainActivityViewModel by activityViewModels<MainActivityViewModel>()
    private val scanViewModel by viewModels<ScanViewModel>()

    private lateinit var bleManager:BleManager

    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.menu_scan, menu)
            menu.findItem(R.id.action_scan)?.let { actionScan ->
                lifecycleScope.launch {
                    scanViewModel.stateFLowScanState.collect { state ->
                        when (state) {
                            BleScanManager.State.Stopped -> {
                                actionScan.title = getString(R.string.scan_start)
                                actionScan.setIcon(R.drawable.ic_scan)
                            }
                            BleScanManager.State.Scanning -> {
                                actionScan.title = getString(R.string.scan_stop)
                                actionScan.setIcon(R.drawable.ic_stop)
                            }
                            BleScanManager.State.Error -> {
                                actionScan.title = getString(R.string.scan_error, scanViewModel.scanError)
                                actionScan.setIcon(R.drawable.ic_error)
                            }
                        }
                    }
                }
            }
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when(menuItem.itemId) {
                R.id.action_scan -> {
                    when(scanViewModel.scanState) {
                        BleScanManager.State.Stopped -> {
                            bleManager.startScan(stopTimeout = 10000L)
                            scanViewModel.scanPressed = true
                        }
                        BleScanManager.State.Scanning -> {
                            bleManager.stopScan()
                            scanViewModel.scanPressed = false
                        }
                        BleScanManager.State.Error -> {
                            scanViewModel.scanPressed = false
                        }
                    }
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentScanBinding.inflate(inflater, container, false)
        bleManager = (requireContext().applicationContext as LessonBle03App).bleManager!!
        scanViewModel.changeBleManager(bleManager)

        Log.d(logTag, "onCreateView(${(requireContext().applicationContext as LessonBle03App).bleManager})")
        linkMenu(true, menuProvider)
        binding.apply {
            rvBtDevices.adapter = rvBtAdapter
            rvBtDevices.layoutManager = LinearLayoutManager(requireContext())
        }

        rvBtAdapter.setItemOnClickListener { scanResult, _ ->
            mainActivityViewModel.changeScanResult(scanResult)
            findNavController().navigate(R.id.action_scanFragment_to_deviceFragment)
        }

        lifecycleScope.launch {
            bleManager.sharedFlowScanReulst.collect { bluetoothDevice ->
                rvBtAdapter.addScanResult(bluetoothDevice)
            }
        }

        if (scanViewModel.scanPressed) {
            bleManager.startScan(stopTimeout = 10000L)
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(logTag, "onViewCreated(${(requireContext().applicationContext as LessonBle03App).bleManager})")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        linkMenu(false, menuProvider)
        bleManager.stopScan()
        _binding = null
    }
}