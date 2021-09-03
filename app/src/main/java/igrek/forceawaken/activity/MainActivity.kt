package igrek.forceawaken.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import igrek.forceawaken.info.logger.Logger
import igrek.forceawaken.info.logger.LoggerFactory
import igrek.forceawaken.inject.AppContextFactory
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory
import igrek.forceawaken.util.RetryDelayed


open class MainActivity(
        mainActivityData: LazyInject<MainActivityData> = appFactory.activityData,
) : AppCompatActivity() {
    private var activityData by LazyExtractor(mainActivityData)

    private val logger: Logger = LoggerFactory.logger

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            logger.info("Creating Dependencies container...")
            AppContextFactory.createAppContext(this)
            recreateFields() // Workaround for reusing finished activities by Android
            super.onCreate(savedInstanceState)
            activityData.mainActivityLayout.init()
            activityData.inflate()
        } catch (t: Throwable) {
            logger.fatal(t)
            throw t
        }
    }

    private fun recreateFields() {
        activityData = appFactory.activityData.get()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        activityData.activityController.onConfigurationChanged(newConfig)
    }

    override fun onDestroy() {
        super.onDestroy()
        activityData.activityController.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        Handler(Looper.getMainLooper()).post {
            RetryDelayed(10, 500, UninitializedPropertyAccessException::class.java) {
                activityData.activityController.onStart()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        activityData.activityController.onStop()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return activityData.optionSelectDispatcher.optionsSelect(item.itemId) || super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                if (activityData.systemKeyDispatcher.onKeyBack())
                    return true
            }
            KeyEvent.KEYCODE_MENU -> {
                if (activityData.systemKeyDispatcher.onKeyMenu())
                    return true
            }
            KeyEvent.KEYCODE_VOLUME_UP -> {
                if (activityData.systemKeyDispatcher.onVolumeUp())
                    return true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                if (activityData.systemKeyDispatcher.onVolumeDown())
                    return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return super.onKeyDown(keyCode, event)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        activityData.permissionService.onRequestPermissionsResult(permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        activityData.activityResultDispatcher.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

}
