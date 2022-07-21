package com.pikhto.lessonble03.ui

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.lifecycleScope
import com.pikhto.blin.BleManager
import com.pikhto.blin.permission.RequestPermissions
import com.pikhto.lessonble03.BleApp03
import com.pikhto.lessonble03.R
import com.pikhto.lessonble03.databinding.ActivityMainBinding
import com.pikhto.lessonble03.ui.models.MainActivityViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private val logTag = this.javaClass.simpleName
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val mainActivityViewModel by viewModels<MainActivityViewModel>()

    private val bleManager:BleManager by lazy {
        BleManager(applicationContext).let {
            (applicationContext as BleApp03).bleManager = it
            it
        }
    }

    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.menu_main, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.action_settings -> true
                else -> { false }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycle.addObserver(bleManager)
        Log.d(logTag, "bleManager = ${(applicationContext as BleApp03).bleManager}")

        requestPermissions()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        linkMenu(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        linkMenu(false)
        super.onDestroy()
    }

    private fun linkMenu(link: Boolean = true) {
        (this as MenuHost).let {
            if(link) {
                it.addMenuProvider(menuProvider)
            } else {
                it.removeMenuProvider(menuProvider)
            }
        }
    }

    private fun requestPermissions() {
        val requestPermissions = RequestPermissions(this)
        requestPermissions.requestPermissions(listOf(
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.ACCESS_FINE_LOCATION",
        ))

        lifecycleScope.launch {
            requestPermissions.stateFlowRequestPermission.filterNotNull().collect { permission ->
                if (permission.granted) {
                    Toast.makeText(baseContext, getString(R.string.permission_granted, permission.permission), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(baseContext, getString(R.string.permission_not_granted, permission.permission), Toast.LENGTH_SHORT).show()
                    finishAndRemoveTask()
                    exitProcess(0)
                }
            }
        }
    }
}