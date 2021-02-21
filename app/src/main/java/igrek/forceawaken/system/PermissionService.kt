package igrek.forceawaken.system

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import igrek.forceawaken.info.logger.LoggerFactory
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory

class PermissionService(
        activity: LazyInject<Activity> = appFactory.activity,
) {
    private val activity by LazyExtractor(activity)

    private val logger = LoggerFactory.logger

    val isStoragePermissionGranted: Boolean
        get() = isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val isMicrophonePermissionGranted: Boolean
        get() = isPermissionGranted(Manifest.permission.RECORD_AUDIO)

    private fun isPermissionGranted(permission: String): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                ActivityCompat.requestPermissions(activity, arrayOf(permission), 1)
                false
            }
        } else {
            true //permission is automatically granted on sdk<23 upon installation
        }
    }

    private fun onPermissionGranted(permission: String) {
        logger.info("permission $permission has been granted")
    }

    private fun onPermissionDenied(permission: String) {
        logger.warn("permission $permission has been denied")
    }

    fun onRequestPermissionsResult(permissions: Array<String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permissions[0])
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                onPermissionDenied(permissions[0])
            }
        }
    }
}
