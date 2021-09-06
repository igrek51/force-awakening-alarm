package igrek.forceawaken.activity.settings

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import igrek.forceawaken.info.logger.Logger
import igrek.forceawaken.info.logger.LoggerFactory
import igrek.forceawaken.inject.AppContextFactory
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory


open class SettingsActivity(
    settingsActivityData: LazyInject<SettingsActivityData> = appFactory.settingsActivityData,
) : AppCompatActivity() {
    private var activityData by LazyExtractor(settingsActivityData)

    private val logger: Logger = LoggerFactory.logger
    private var initialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            initialized = false
            logger.info("Creating Dependencies container...")
            AppContextFactory.createAppContext(this)
            recreateFields() // Workaround for reusing finished activities by Android
            super.onCreate(savedInstanceState)
            activityData.settingsActivityLayout.init()
            activityData.inflate()
            initialized = true
        } catch (t: Throwable) {
            logger.fatal(t)
            throw t
        }
    }

    private fun recreateFields() {
        activityData = appFactory.settingsActivityData.get()
    }

    override fun onStart() {
        super.onStart()
        if (initialized) {
            activityData.userDataDao.requestSave(false)
        }
    }

    override fun onStop() {
        super.onStop()
        if (initialized) {
            activityData.preferencesService.saveAll()
            activityData.userDataDao.saveNow()
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        activityData.activityResultDispatcher.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

}
