package dev.legendtuner.app

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import dev.legendtuner.app.access.ShizukuManager
import dev.legendtuner.app.ui.LegendTunerApp
import dev.legendtuner.app.ui.theme.LegendTunerTheme
import rikka.shizuku.Shizuku

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    private val shizukuPermissionListener =
        Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
            if (requestCode == SHIZUKU_REQUEST_CODE) {
                viewModel.onShizukuPermissionResult(grantResult == PackageManager.PERMISSION_GRANTED)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        Shizuku.addRequestPermissionResultListener(shizukuPermissionListener)
        setContent {
            LegendTunerTheme {
                LegendTunerApp(
                    viewModel = viewModel,
                    onRequestShizuku = ::requestShizukuPermission
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshAccessStatus()
    }

    private fun requestShizukuPermission() {
        if (!ShizukuManager.isBinderAlive()) {
            viewModel.showMessage(
                "Shizuku is not running. Open Shizuku and start it with wireless debugging first.",
                error = true
            )
            return
        }
        if (ShizukuManager.hasPermission()) {
            viewModel.connectShizuku()
        } else {
            ShizukuManager.requestPermission(SHIZUKU_REQUEST_CODE)
        }
    }

    override fun onDestroy() {
        Shizuku.removeRequestPermissionResultListener(shizukuPermissionListener)
        super.onDestroy()
    }

    companion object {
        private const val SHIZUKU_REQUEST_CODE = 1001
    }
}
